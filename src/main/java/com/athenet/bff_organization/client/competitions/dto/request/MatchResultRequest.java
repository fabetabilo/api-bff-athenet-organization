package com.athenet.bff_organization.client.competitions.dto.request;

/** Body de PUT /api/delegate/matches/{matchId}/result */
public record MatchResultRequest(
		Integer homeScore,
		Integer awayScore) {
}
