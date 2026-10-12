package com.athenet.bff_organization.client.events.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * EventRequest corresponde al body que recibe el microservicio en POST/PUT /api/admin/events.
 * Copia fiel: ms-events rechaza con 400 cualquier campo que no este aqui, y espera las horas en HH:mm.
 */
public record EventRequest(
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
		Long sportId,
		Long organizerInstitutionId,
		Long organizerCampusId,
		Long organizerTeamId,
		Long teamOneId,
		Long teamTwoId,
		String delegateOid,
		String delegateDisplayName) {
}
