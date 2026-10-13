package com.athenet.bff_organization.web.error;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.athenet.bff_organization.service.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;

/**
 * Traduce toda excepcion a ApiError con el status correcto.
 *
 * - ServiceException: errores de los services y de los microservicios (traducidos en RestClientConfig).
 * - ResourceAccessException: microservicio caido o sin responder a tiempo → 503.
 * - Excepciones de Spring MVC (body ilegible, parametro mal tipado, etc.): las resuelve ResponseEntityExceptionHandler.
 * - 401/403 de seguridad: los resuelve Spring Security, sin body.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<Object> handleService(ServiceException ex) {
		List<ApiError.FieldError> fieldErrors = ex.getFieldErrors().stream()
				.map(f -> new ApiError.FieldError(f.field(), f.code(), f.message()))
				.toList();
		return build(statusOf(ex.getType()), new HttpHeaders(), ex.getCode(), ex.getMessage(), fieldErrors);
	}

	// No pasa por el status handler de RestClientConfig: el microservicio no llego a responder
	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<Object> handleResourceAccess(ResourceAccessException ex) {
		log.warn("Microservicio no disponible: {}", ex.getMessage());
		return build(HttpStatus.SERVICE_UNAVAILABLE, new HttpHeaders(), "SERVICE_UNAVAILABLE",
				"Un servicio no responde. Intenta nuevamente en unos minutos.", List.of());
	}

	/** Se relanzan para que Spring Security responda 401/403 sin body (p. ej. desde un @PreAuthorize) */
	@ExceptionHandler({ AccessDeniedException.class, AuthenticationException.class })
	public void rethrowSecurity(RuntimeException ex) {
		throw ex;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpected(Exception ex) {
		log.error("Error no controlado", ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, new HttpHeaders(), "INTERNAL_ERROR",
				"Ocurrió un error inesperado.", List.of());
	}

	/** Body ilegible: JSON invalido, fecha u hora con otro formato. Mismo code y fieldErrors que ms-events */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		// La excepcion de Jackson (la que trae la ruta del campo) puede venir envuelta
		JacksonException jackson = null;
		for (Throwable t = ex; t != null && jackson == null; t = t.getCause()) {
			if (t instanceof JacksonException je) jackson = je;
		}
		String field = jackson != null ? pathOf(jackson) : null;
		List<ApiError.FieldError> fieldErrors = field != null
				? List.of(new ApiError.FieldError(field, "INVALID_FORMAT", "Formato inválido (fechas yyyy-MM-dd, horas HH:mm)."))
				: List.of();
		return build(HttpStatus.BAD_REQUEST, headers, "MALFORMED_REQUEST", "El cuerpo de la petición no es válido.", fieldErrors);
	}

	/** Resto de excepciones de Spring MVC: mismo status, body ApiError en vez de ProblemDetail */
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		HttpStatus status = HttpStatus.valueOf(statusCode.value());
		String message = body instanceof ProblemDetail problem && problem.getDetail() != null
				? problem.getDetail()
				: status.getReasonPhrase();
		return build(status, headers, status.name(), message, List.of());
	}

	private static HttpStatus statusOf(ServiceException.Type type) {
		return switch (type) {
			case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
			case FORBIDDEN -> HttpStatus.FORBIDDEN;
			case NOT_FOUND -> HttpStatus.NOT_FOUND;
			case CONFLICT -> HttpStatus.CONFLICT;
			case VALIDATION_FAILED -> HttpStatus.UNPROCESSABLE_CONTENT;
			case UPSTREAM_ERROR -> HttpStatus.BAD_GATEWAY;
			case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
		};
	}

	/** Ruta del campo culpable en notacion del body: startTime, photos[2] */
	private static String pathOf(JacksonException ex) {
		StringBuilder path = new StringBuilder();
		for (JacksonException.Reference ref : ex.getPath()) {
			if (ref.getPropertyName() != null) {
				if (!path.isEmpty()) path.append('.');
				path.append(ref.getPropertyName());
			} else if (ref.getIndex() >= 0) {
				path.append('[').append(ref.getIndex()).append(']');
			}
		}
		return path.isEmpty() ? null : path.toString();
	}

	private static ResponseEntity<Object> build(HttpStatus status, HttpHeaders headers, String code, String message,
			List<ApiError.FieldError> fieldErrors) {
		ApiError body = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(), code, message, fieldErrors);
		return ResponseEntity.status(status).headers(headers).body(body);
	}

}
