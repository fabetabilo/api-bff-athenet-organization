package com.athenet.bff_organization.web.director.dto.response;

import java.time.LocalDate;

/**
 * Competencia que ve el director, en cualquier estado. status: DRAFT, IN_PROGRESS o FINISHED.
 * Si el deporte ya no existe en ms-institutions, viene con su id y name en null.
 */
public record DirectorCompetitionResponse(
		Long id,
		String name,
		String type,
		Sport sport,
		String season,
		String description,
		String competitionImg,
		String status,
		LocalDate startDate,
		LocalDate endDate) {

	public record Sport(Long id, String name) {}

}
