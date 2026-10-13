package com.athenet.bff_organization.service.exception;

import java.util.List;

import lombok.Getter;

/**
 * Error de un caso de uso, sin tipos HTTP: web.error traduce el Type a un status.
 * La lanzan los services (reglas propias) y la traduccion de errores de los clients (ver RestClientConfig).
 */
@Getter
public class ServiceException extends RuntimeException {

	public enum Type {
		BAD_REQUEST,
		FORBIDDEN,
		NOT_FOUND,
		CONFLICT,
		VALIDATION_FAILED,
		UPSTREAM_ERROR,
		SERVICE_UNAVAILABLE
	}

	/** Error atribuible a un campo; field es la clave exacta del body que envio el frontend */
	public record FieldDetail(String field, String code, String message) {}

	private final Type type;
	private final String code;
	private final List<FieldDetail> fieldErrors;

	public ServiceException(Type type, String code, String message, List<FieldDetail> fieldErrors) {
		super(message);
		this.type = type;
		this.code = code;
		this.fieldErrors = fieldErrors != null ? fieldErrors : List.of();
	}

	public ServiceException(Type type, String code, String message) {
		this(type, code, message, List.of());
	}

}
