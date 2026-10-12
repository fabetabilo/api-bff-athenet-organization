package com.athenet.bff_organization.client.events.dto.response;

/** Tipo de evento que entrega el microservicio. requiresMatchup indica si lleva dos equipos (partido) u organizador */
public record EventTypeResponse(
		String code,
		String label,
		boolean requiresMatchup) {
}
