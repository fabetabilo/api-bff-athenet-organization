package com.athenet.bff_organization.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import com.athenet.bff_organization.service.exception.ServiceException;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@Configuration
public class RestClientConfig {

	@Bean
	public RestClient eventsRestClient(RestClient.Builder builder, CircuitBreakerRegistry circuitBreakers,
			@Value("${athenet.ms.events.url}") String eventsUrl) {
		return builder.baseUrl(eventsUrl)
				.requestInterceptor(circuitBreaker(circuitBreakers.circuitBreaker("events")))
				.requestInterceptor(new BearerTokenRelay())
				.build();
	}

	/** Sin bearerTokenRelay: sus GET son publicos, y un bearer vencido o invalido los haria responder 401 */
	@Bean
	public RestClient institutionsRestClient(RestClient.Builder builder, CircuitBreakerRegistry circuitBreakers,
			@Value("${athenet.ms.institutions.url}") String institutionsUrl) {
		return builder.baseUrl(institutionsUrl)
				.requestInterceptor(circuitBreaker(circuitBreakers.circuitBreaker("institutions")))
				.build();
	}

	/** Cliente de escritura de Instituciones, con el token del usuario ADMIN. */
	@Bean
	public RestClient adminInstitutionsRestClient(RestClient.Builder builder, CircuitBreakerRegistry circuitBreakers,
			@Value("${athenet.ms.institutions.url}") String institutionsUrl) {
		return builder.baseUrl(institutionsUrl)
				.requestInterceptor(circuitBreaker(circuitBreakers.circuitBreaker("institutions")))
				.requestInterceptor(new BearerTokenRelay())
				.build();
	}

	/** Con bearerTokenRelay: ms-competitions autoriza por rol y compara el oid del delegado */
	@Bean
	public RestClient competitionsRestClient(RestClient.Builder builder, CircuitBreakerRegistry circuitBreakers,
			@Value("${athenet.ms.competitions.url}") String competitionsUrl) {
		return builder.baseUrl(competitionsUrl)
				.requestInterceptor(circuitBreaker(circuitBreakers.circuitBreaker("competitions")))
				.requestInterceptor(new BearerTokenRelay())
				.build();
	}

	/**
	 * Con el circuito abierto responde 503 sin llamar al microservicio. Cuentan como falla las caidas, timeouts y
	 * 5xx; un 4xx es una respuesta valida del microservicio (validacion, no encontrado, conflicto) y no lo abre.
	 */
	private static ClientHttpRequestInterceptor circuitBreaker(CircuitBreaker circuitBreaker) {
		return (request, body, execution) -> {
			if (!circuitBreaker.tryAcquirePermission()) {
				throw new ServiceException(ServiceException.Type.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE",
						"Un servicio no responde. Intenta nuevamente en unos minutos.");
			}
			long start = circuitBreaker.getCurrentTimestamp();
			try {
				ClientHttpResponse response = execution.execute(request, body);
				long duration = circuitBreaker.getCurrentTimestamp() - start;
				if (response.getStatusCode().is5xxServerError()) {
					circuitBreaker.onError(duration, circuitBreaker.getTimestampUnit(), new IOException(
							request.getMethod() + " " + request.getURI() + " respondio " + response.getStatusCode().value()));
				} else {
					circuitBreaker.onSuccess(duration, circuitBreaker.getTimestampUnit());
				}
				return response;
			} catch (IOException | RuntimeException ex) {
				// Caido o timeout: se registra y se relanza (termina en ResourceAccessException → 503)
				circuitBreaker.onError(circuitBreaker.getCurrentTimestamp() - start, circuitBreaker.getTimestampUnit(), ex);
				throw ex;
			}
		};
	}

}
