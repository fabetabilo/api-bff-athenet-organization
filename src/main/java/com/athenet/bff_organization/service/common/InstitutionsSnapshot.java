package com.athenet.bff_organization.service.common;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.athenet.bff_organization.client.institutions.dto.response.DeporteResponse;
import com.athenet.bff_organization.client.institutions.dto.response.EquipoResponse;
import com.athenet.bff_organization.client.institutions.dto.response.InstitucionResponse;
import com.athenet.bff_organization.client.institutions.dto.response.SedeResponse;

/**
 * Catalogos completos de ms-institutions (con inactivos) indexados por id, para resolver en memoria las
 * referencias de uno o varios eventos. Cada busqueda devuelve null si el id es null o ya no existe.
 */
public record InstitutionsSnapshot(
		Map<Long, DeporteResponse> deportes,
		Map<Long, InstitucionResponse> instituciones,
		Map<Long, SedeResponse> sedes,
		Map<Long, EquipoResponse> equipos) {

	public static InstitutionsSnapshot of(List<DeporteResponse> deportes, List<InstitucionResponse> instituciones,
			List<SedeResponse> sedes, List<EquipoResponse> equipos) {
		return new InstitutionsSnapshot(
				byId(deportes, DeporteResponse::id),
				byId(instituciones, InstitucionResponse::id),
				byId(sedes, SedeResponse::id),
				byId(equipos, EquipoResponse::id));
	}

	/** Sin catalogos: toda referencia se resuelve con su id y name en null */
	public static InstitutionsSnapshot empty() {
		return of(List.of(), List.of(), List.of(), List.of());
	}

	public DeporteResponse deporte(Long id) {
		return find(deportes, id);
	}

	public InstitucionResponse institucion(Long id) {
		return find(instituciones, id);
	}

	public SedeResponse sede(Long id) {
		return find(sedes, id);
	}

	public EquipoResponse equipo(Long id) {
		return find(equipos, id);
	}

	private static <T> Map<Long, T> byId(List<T> items, Function<T, Long> id) {
		return items.stream().collect(Collectors.toUnmodifiableMap(id, Function.identity()));
	}

	// Los mapas inmutables lanzan NullPointerException con get(null)
	private static <T> T find(Map<Long, T> byId, Long id) {
		return id != null ? byId.get(id) : null;
	}

}
