package com.athenet.bff_organization.service.common;

import java.util.List;

import org.springframework.stereotype.Service;

import com.athenet.bff_organization.service.mock.InstitutionsMock;
import com.athenet.bff_organization.web.common.dto.response.CampusOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.InstitutionOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.SportOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.TeamOptionResponse;

import lombok.RequiredArgsConstructor;

/** Catalogos compartidos (common) entre roles: deportes, instituciones, sedes y equipos */
@Service
@RequiredArgsConstructor
public class CatalogService {

	// TEMPORAL: por ahora usa el institutions mock, a quien concierne la implementacion de este, DEBE modificarlo
	private final InstitutionsMock institutionsMock;

	public List<SportOptionResponse> getSports() {
		return institutionsMock.getSports();
	}

	public List<InstitutionOptionResponse> getInstitutions(Long sportId) {
		return institutionsMock.getInstitutions(sportId);
	}

	public List<CampusOptionResponse> getCampuses(Long institutionId, Long sportId) {
		return institutionsMock.getCampuses(institutionId, sportId);
	}

	public List<TeamOptionResponse> getTeams(Long campusId, Long sportId, String division) {
		return institutionsMock.getTeams(campusId, sportId, division);
	}

}
