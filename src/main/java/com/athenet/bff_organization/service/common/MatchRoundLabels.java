package com.athenet.bff_organization.service.common;

/** Nombre en pantalla de cada ronda de la copa (MatchResponse.round de ms-competitions) */
public final class MatchRoundLabels {

	private MatchRoundLabels() {
	}

	public static String of(String round) {
		if (round == null) return null;
		return switch (round) {
			case "ROUND_OF_16" -> "Octavos de final";
			case "QUARTER_FINAL" -> "Cuartos de final";
			case "SEMI_FINAL" -> "Semifinal";
			case "FINAL" -> "Final";
			default -> round;
		};
	}

}
