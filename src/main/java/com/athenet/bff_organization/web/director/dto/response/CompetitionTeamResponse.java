package com.athenet.bff_organization.web.director.dto.response;

/** Fila de la nomina de una competencia. Si la institucion ya no existe, viene con su id y name en null */
public record CompetitionTeamResponse(
		Long teamId,
		String name,
		String logoUrl,
		Institution institution) {

	public record Institution(Long id, String name, String acronym) {}

}
