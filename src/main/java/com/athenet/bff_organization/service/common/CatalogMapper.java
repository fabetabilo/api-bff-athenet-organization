package com.athenet.bff_organization.service.common;

import java.util.List;

import org.springframework.stereotype.Component;

import com.athenet.bff_organization.client.institutions.dto.response.DeporteResponse;
import com.athenet.bff_organization.client.institutions.dto.response.EquipoResponse;
import com.athenet.bff_organization.client.institutions.dto.response.InstitucionResponse;
import com.athenet.bff_organization.client.institutions.dto.response.SedeResponse;
import com.athenet.bff_organization.web.common.dto.response.CampusOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.InstitutionOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.SportOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.TeamOptionResponse;

/** Catalogos de ms-institutions → opciones de dropdown. No filtra ni ordena: eso lo decide CatalogService */
@Component
public class CatalogMapper {

	public List<SportOptionResponse> toSportOptions(List<DeporteResponse> deportes) {
		return deportes.stream()
				.map(d -> new SportOptionResponse(d.id(), d.nombre()))
				.toList();
	}

	public List<InstitutionOptionResponse> toInstitutionOptions(List<InstitucionResponse> instituciones) {
		return instituciones.stream()
				.map(i -> new InstitutionOptionResponse(i.id(), i.nombre(), i.sigla()))
				.toList();
	}

	public List<CampusOptionResponse> toCampusOptions(List<SedeResponse> sedes) {
		return sedes.stream()
				.map(s -> new CampusOptionResponse(s.id(), s.nombre()))
				.toList();
	}

	public List<TeamOptionResponse> toTeamOptions(List<EquipoResponse> equipos) {
		return equipos.stream()
				.map(e -> new TeamOptionResponse(e.id(), e.nombre(), e.categoria()))
				.toList();
	}

}
