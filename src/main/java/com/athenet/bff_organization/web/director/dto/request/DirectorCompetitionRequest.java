package com.athenet.bff_organization.web.director.dto.request;

import java.time.LocalDate;

/** Body para crear o editar una competencia. No lleva tipo: por ahora toda competencia es una copa */
public record DirectorCompetitionRequest(
		String name,
		Long sportId,
		String season,
		String description,
		String competitionImg,
		LocalDate startDate,
		LocalDate endDate) {
}
