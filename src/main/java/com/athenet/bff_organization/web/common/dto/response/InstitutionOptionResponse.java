package com.athenet.bff_organization.web.common.dto.response;

/** Opcion del dropdown "Institucion" */
public record InstitutionOptionResponse(
		Long id,
		String name,
		String acronym) {
}
