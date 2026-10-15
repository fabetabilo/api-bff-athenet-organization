package com.athenet.bff_organization.client.competitions.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Partido que entrega el microservicio. round: ROUND_OF_16, QUARTER_FINAL, SEMI_FINAL o FINAL;
 * status: SCHEDULED, IN_PROGRESS o FINISHED. Los equipos van en null hasta definirse.
 */
public record MatchResponse(
		Long id,
		Long competitionId,
		String round,
		int position,
		LocalDate date,
		LocalTime time,
		String location,
		String address,
		Long homeTeamId,
		Long awayTeamId,
		String delegateOid,
		String delegateDisplayName,
		String status,
		Integer homeScore,
		Integer awayScore,
		int version) {
}
