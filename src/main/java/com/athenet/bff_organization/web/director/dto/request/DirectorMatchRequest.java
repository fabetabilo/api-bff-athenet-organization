package com.athenet.bff_organization.web.director.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Body para programar un partido: el partido completo, lo que no se define en null. Los equipos solo se
 * eligen en octavos con la copa en DRAFT; en las demas rondas se envian tal como vienen.
 */
public record DirectorMatchRequest(
		LocalDate date,
		@JsonFormat(pattern = "HH:mm") LocalTime time,
		String location,
		String address,
		Long homeTeamId,
		Long awayTeamId,
		String delegateOid,
		String delegateDisplayName) {
}
