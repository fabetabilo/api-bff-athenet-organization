package com.athenet.bff_organization.client.institutions.dto.response;

/** Sede que entrega ms-institutions */
public record SedeResponse(
		Long id,
		String nombre,
		String ciudad,
		String direccion,
		Long institucionId,
		String institucionNombre) {
}
