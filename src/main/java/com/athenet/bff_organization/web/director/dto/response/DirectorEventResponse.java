package com.athenet.bff_organization.web.director.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Evento que ve el director, en cualquier estado: igual que el body de escritura, pero con las
 * referencias resueltas. Partido: teamOne y teamTwo; otro tipo: organizer.
 * Si una referencia ya no existe, viene con su id y name en null.
 * Con competitionMatchId es copia de un partido de una competencia: de solo lectura, se edita en la competencia.
 */
public record DirectorEventResponse(
		String internalId,
		String title,
		String description,
		String descriptionOpt,
		String type,
		String status,
		Boolean isOfficial,
		LocalDate eventDate,
		@JsonFormat(pattern = "HH:mm") LocalTime startTime,
		LocalDate endDate,
		@JsonFormat(pattern = "HH:mm") LocalTime endTime,
		String location,
		String address,
		String coverImage,
		List<String> photos,
		Sport sport,
		Organizer organizer,
		MatchTeam teamOne,
		MatchTeam teamTwo,
		Delegate delegate,
		Long competitionId,
		Long competitionMatchId) {

	public record Sport(Long id, String name) {}

	public record Institution(Long id, String name, String acronym) {}

	public record Campus(Long id, String name) {}

	public record Team(Long id, String name, String division) {}

	public record Organizer(Institution institution, Campus campus, Team team) {}

	public record MatchTeam(Long id, String name, String division, Campus campus, Institution institution) {}

	public record Delegate(String oid, String displayName) {}

}
