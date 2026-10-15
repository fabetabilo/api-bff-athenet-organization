package com.athenet.bff_organization.client.competitions.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Body de PUT /api/admin/competitions/{id}/matches/{matchId}: el partido completo, lo que no se define en null.
 * El microservicio espera la hora en HH:mm.
 */
public record MatchUpdateRequest(
		LocalDate date,
		@JsonFormat(pattern = "HH:mm") LocalTime time,
		String location,
		String address,
		Long homeTeamId,
		Long awayTeamId,
		String delegateOid,
		String delegateDisplayName) {
}
