package com.athenet.bff_organization.web.director.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Body que envia el frontend para crear o editar un evento: plano, solo IDs, lo que no aplica en null.
 * teamXInstitutionId y teamXCampusId no se reenvian a ms-events.
 */
public record DirectorEventRequest(
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
		Long teamOneInstitutionId,
		Long teamOneCampusId,
		Long teamOneId,
		Long teamTwoInstitutionId,
		Long teamTwoCampusId,
		Long teamTwoId,
		String delegateOid,
		String delegateDisplayName) {
}
