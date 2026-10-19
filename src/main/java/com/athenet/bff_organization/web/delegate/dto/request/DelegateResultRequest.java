package com.athenet.bff_organization.web.delegate.dto.request;

/** Resultado final del partido (o su correccion). No hay empates */
public record DelegateResultRequest(
		Integer homeScore,
		Integer awayScore) {
}
