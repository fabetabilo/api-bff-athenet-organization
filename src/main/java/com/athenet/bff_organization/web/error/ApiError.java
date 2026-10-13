package com.athenet.bff_organization.web.error;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato de error hacia el frontend. Es el mismo que usa ms-events, asi su code, message y fieldErrors se reenvian tal cual.
 */
public record ApiError(
		LocalDateTime timestamp,
		int status,
		String error,
		String code,
		String message,
		List<FieldError> fieldErrors) {

	/** field es la clave exacta del body, para que el frontend pinte el error en su campo */
	public record FieldError(String field, String code, String message) {}

}
