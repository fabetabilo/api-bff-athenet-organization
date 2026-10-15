package com.athenet.bff_organization.client.competitions.dto.response;

import java.time.LocalDate;

/** Competencia que entrega el microservicio. type: LEAGUE o CUP; status: DRAFT, IN_PROGRESS o FINISHED */
public record CompetitionResponse(
		Long id,
		String name,
		String type,
		Long sportId,
		String season,
		String description,
		String competitionImg,
		String status,
		LocalDate startDate,
		LocalDate endDate,
		int winPoints,
		int drawPoints,
		int lossPoints) {
}
