package com.athenet.bff_organization.client.competitions.dto.response;

/** Equipo inscrito que entrega el microservicio: copia de nombre, logo e institucion de ms-institutions */
public record EnrollmentResponse(
		Long id,
		Long competitionId,
		Long teamId,
		Long institutionId,
		String teamName,
		String teamLogoUrl) {
}
