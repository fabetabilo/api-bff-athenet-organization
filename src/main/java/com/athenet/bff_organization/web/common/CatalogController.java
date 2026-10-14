package com.athenet.bff_organization.web.common;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.athenet.bff_organization.service.common.CatalogService;
import com.athenet.bff_organization.web.common.dto.response.CampusOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.InstitutionOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.SportOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.TeamOptionResponse;

/** Catalogos compartidos entre roles (ADMIN, DIRECTOR, DELEGATE). En equipos, sportId y division son opcionales */
@RestController
@RequestMapping("/api/common")
public class CatalogController {

	private final CatalogService catalogService;

	public CatalogController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	@GetMapping("/sports")
	public List<SportOptionResponse> getSports() {
		return catalogService.getSports();
	}

	@GetMapping("/institutions")
	public List<InstitutionOptionResponse> getInstitutions() {
		return catalogService.getInstitutions();
	}

	@GetMapping("/institutions/{institutionId}/campuses")
	public List<CampusOptionResponse> getCampuses(@PathVariable Long institutionId) {
		return catalogService.getCampuses(institutionId);
	}

	@GetMapping("/campuses/{campusId}/teams")
	public List<TeamOptionResponse> getTeams(@PathVariable Long campusId,
			@RequestParam(required = false) Long sportId,
			@RequestParam(required = false) String division) {
		return catalogService.getTeams(campusId, sportId, division);
	}
}
