package com.athenet.bff_organization.client.institutions.dto.response;

/** Institucion que entrega ms-institutions */
public record InstitucionResponse(
		Long id,
		String nombre,
		String sigla,
		String imagenUrl,
		boolean activo) {
}
