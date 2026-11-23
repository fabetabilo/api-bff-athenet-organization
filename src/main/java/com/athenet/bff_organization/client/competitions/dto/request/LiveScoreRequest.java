package com.athenet.bff_organization.client.competitions.dto.request;

/** Body de POST /api/delegate/matches/{matchId}/score */
public record LiveScoreRequest(
		Integer homeScore,
		Integer awayScore) {
}
