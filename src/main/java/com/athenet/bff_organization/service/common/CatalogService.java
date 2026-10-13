package com.athenet.bff_organization.service.common;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.athenet.bff_organization.client.institutions.InstitutionsClient;
import com.athenet.bff_organization.client.institutions.dto.response.DeporteResponse;
import com.athenet.bff_organization.client.institutions.dto.response.EquipoResponse;
import com.athenet.bff_organization.client.institutions.dto.response.InstitucionResponse;
import com.athenet.bff_organization.client.institutions.dto.response.SedeResponse;
import com.athenet.bff_organization.service.exception.ServiceException;
import com.athenet.bff_organization.web.common.dto.response.CampusOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.InstitutionOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.SportOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.TeamOptionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Catalogos compartidos (common) entre roles: deportes, instituciones, sedes y equipos (ms-institutions).
 * Los dropdowns ocultan instituciones y equipos inactivos y se ordenan por nombre.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

	// Orden alfabetico en español: tildes y ñ en su lugar
	private static final Collator SPANISH = Collator.getInstance(Locale.forLanguageTag("es"));

	private final InstitutionsClient institutionsClient;
	private final CatalogMapper catalogMapper;

	public List<SportOptionResponse> getSports() {
		return catalogMapper.toSportOptions(institutionsClient.getDeportes().stream()
				.sorted(byName(DeporteResponse::nombre))
				.toList());
	}

	public List<InstitutionOptionResponse> getInstitutions() {
		return catalogMapper.toInstitutionOptions(institutionsClient.getInstituciones().stream()
				.filter(InstitucionResponse::activo)
				.sorted(byName(InstitucionResponse::nombre))
				.toList());
	}

	public List<CampusOptionResponse> getCampuses(Long institutionId) {
		return catalogMapper.toCampusOptions(institutionsClient.getSedesByInstitucion(institutionId).stream()
				.sorted(byName(SedeResponse::nombre))
				.toList());
	}

	/** sportId y division son opcionales (null = sin filtro) */
	public List<TeamOptionResponse> getTeams(Long campusId, Long sportId, String division) {
		return catalogMapper.toTeamOptions(institutionsClient.getEquiposBySede(campusId).stream()
				.filter(EquipoResponse::activo)
				.filter(e -> sportId == null || sportId.equals(e.deporteId()))
				.filter(e -> division == null || division.equalsIgnoreCase(e.categoria()))
				.sorted(byName(EquipoResponse::nombre))
				.toList());
	}

	/** Los cuatro catalogos completos, con inactivos, para resolver las referencias de los eventos */
	public InstitutionsSnapshot getInstitutionsSnapshot() {
		return InstitutionsSnapshot.of(
				institutionsClient.getDeportes(),
				institutionsClient.getInstituciones(),
				institutionsClient.getSedes(),
				institutionsClient.getEquipos());
	}

	/**
	 * Para lecturas: si ms-institutions no responde (caido, timeout, 5xx o circuito abierto), las referencias
	 * van con su id y name en null en vez de tumbar la respuesta. Cualquier otro error se relanza.
	 */
	public InstitutionsSnapshot getInstitutionsSnapshotOrEmpty() {
		try {
			return getInstitutionsSnapshot();
		} catch (ResourceAccessException | ServiceException ex) {
			if (ex instanceof ServiceException se && !isUnavailable(se)) throw se;
			log.warn("ms-institutions no responde; referencias sin nombre: {}", ex.getMessage());
			return InstitutionsSnapshot.empty();
		}
	}

	private static boolean isUnavailable(ServiceException ex) {
		return ex.getType() == ServiceException.Type.UPSTREAM_ERROR
				|| ex.getType() == ServiceException.Type.SERVICE_UNAVAILABLE;
	}

	private static <T> Comparator<T> byName(Function<T, String> name) {
		return Comparator.comparing(name, SPANISH);
	}

}
