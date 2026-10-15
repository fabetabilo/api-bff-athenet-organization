package com.athenet.bff_organization.web.delegate.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Partido de "Mis eventos" del delegado. status: SCHEDULED (puede iniciarlo), IN_PROGRESS (puede finalizarlo)
 * o FINISHED (puede corregirlo). Si un nombre no se pudo resolver, viene en null.
 */
public record DelegateMatchResponse(
		Long id,
		Competition competition,
		String round,
		String roundLabel,
		LocalDate date,
		@JsonFormat(pattern = "HH:mm") LocalTime time,
		String location,
		String address,
		Team homeTeam,
		Team awayTeam,
		String status,
		Integer homeScore,
		Integer awayScore) {

	public record Competition(Long id, String name, String competitionImg) {}

	public record Team(Long id, String name, String logoUrl) {}

}
