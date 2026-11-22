package com.athenet.bff_organization.web.delegate.dto.request;

/** Marcador absoluto en vivo durante un partido en curso (admite empates) */
public record DelegateLiveScoreRequest(
		Integer homeScore,
		Integer awayScore) {
}
