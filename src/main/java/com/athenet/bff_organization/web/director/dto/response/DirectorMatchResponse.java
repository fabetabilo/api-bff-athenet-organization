package com.athenet.bff_organization.web.director.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Fila de la tabla de encuentros. round: ROUND_OF_16, QUARTER_FINAL, SEMI_FINAL o FINAL; status: SCHEDULED,
 * IN_PROGRESS o FINISHED. homeTeam, awayTeam y delegate van en null mientras no se definan.
 */
public record DirectorMatchResponse(
		Long id,
		String round,
		String roundLabel,
		int position,
		LocalDate date,
		@JsonFormat(pattern = "HH:mm") LocalTime time,
		String location,
		String address,
		Team homeTeam,
		Team awayTeam,
		Delegate delegate,
		String status,
		Integer homeScore,
		Integer awayScore) {

	public record Team(Long id, String name, String logoUrl) {}

	public record Delegate(String oid, String displayName) {}

}
