package com.athenet.bff_organization.web.common.dto.response;

/** Opcion del dropdown "Equipo" */
public record TeamOptionResponse(
		Long id,
		String name,
		String division) {
}
