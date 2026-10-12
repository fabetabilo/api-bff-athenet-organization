package com.athenet.bff_organization.web.director.dto.response;

/** Opcion del dropdown "Tipo de evento". requiresMatchup: true = partido (dos equipos y delegado) */
public record EventTypeOptionResponse(
		String code,
		String label,
		boolean requiresMatchup) {
}
