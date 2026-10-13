package com.athenet.bff_organization.client.institutions.dto.response;

/** Equipo que entrega ms-institutions. categoria: MASCULINO, FEMENINO o MIXTO */
public record EquipoResponse(
		Long id,
		String nombre,
		String categoria,
		String entrenador,
		String logoUrl,
		Long sedeId,
		String sedeNombre,
		Long deporteId,
		String deporteNombre,
		boolean activo) {
}
