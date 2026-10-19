package com.athenet.bff_organization.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import com.athenet.bff_organization.service.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.MissingNode;

/** Traducción compartida por clientes públicos y autenticados. */
@Slf4j
@Configuration
public class UpstreamErrorConfig {
	/** Se aplica a todo RestClient.Builder inyectado: cada client nuevo hereda la traduccion de errores */
	@Bean
	RestClientCustomizer upstreamErrorTranslation(JsonMapper jsonMapper) {
		return builder -> builder.defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
			throw toServiceException(jsonMapper, request, response);
		});
	}

	/**
	 * 400/403/404/409/422 se reenvian con el code, message y fieldErrors del microservicio (ApiError).
	 * 401 y el resto (otros 4xx, 5xx) son fallas entre el BFF y el microservicio → 502 generico.
	 * Un 401 no se reenvia: el frontend lo interpretaria como sesion expirada.
	 */
	private static ServiceException toServiceException(JsonMapper jsonMapper, HttpRequest request,
			ClientHttpResponse response) throws IOException {
		int status = response.getStatusCode().value();
		ServiceException.Type type = switch (status) {
			case 400 -> ServiceException.Type.BAD_REQUEST;
			case 403 -> ServiceException.Type.FORBIDDEN;
			case 404 -> ServiceException.Type.NOT_FOUND;
			case 409 -> ServiceException.Type.CONFLICT;
			case 422 -> ServiceException.Type.VALIDATION_FAILED;
			default -> ServiceException.Type.UPSTREAM_ERROR;
		};
		if (type == ServiceException.Type.UPSTREAM_ERROR) {
			log.warn("{} {} respondio {}", request.getMethod(), request.getURI(), status);
			return new ServiceException(type, "UPSTREAM_ERROR", "Un servicio interno respondió con un error.");
		}

		JsonNode body = readBody(jsonMapper, response);
		List<ServiceException.FieldDetail> fieldErrors = new ArrayList<>();
		for (JsonNode f : body.path("fieldErrors")) {
			fieldErrors.add(new ServiceException.FieldDetail(
					f.path("field").asString(null), f.path("code").asString(null), f.path("message").asString(null)));
		}
		return new ServiceException(type,
				body.path("code").asString(type.name()),
				body.path("message").asString("El servicio rechazó la petición."),
				fieldErrors);
	}

	/** Body del error como arbol; si no es JSON (o viene vacio) los campos toman su valor por defecto */
	private static JsonNode readBody(JsonMapper jsonMapper, ClientHttpResponse response) throws IOException {
		try {
			JsonNode body = jsonMapper.readTree(response.getBody());
			return body != null ? body : MissingNode.getInstance();
		} catch (JacksonException e) {
			return MissingNode.getInstance();
		}
	}

}
