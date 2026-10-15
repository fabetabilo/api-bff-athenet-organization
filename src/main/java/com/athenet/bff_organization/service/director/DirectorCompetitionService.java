package com.athenet.bff_organization.service.director;

import java.util.List;

import org.springframework.stereotype.Service;

import com.athenet.bff_organization.client.competitions.CompetitionsClient;
import com.athenet.bff_organization.client.competitions.dto.request.CompetitionStatusRequest;
import com.athenet.bff_organization.client.competitions.dto.response.MatchResponse;
import com.athenet.bff_organization.service.common.CatalogService;
import com.athenet.bff_organization.web.director.dto.request.CompetitionTeamRequest;
import com.athenet.bff_organization.web.director.dto.request.DirectorCompetitionRequest;
import com.athenet.bff_organization.web.director.dto.request.DirectorMatchRequest;
import com.athenet.bff_organization.web.director.dto.response.CompetitionTeamResponse;
import com.athenet.bff_organization.web.director.dto.response.DirectorCompetitionResponse;
import com.athenet.bff_organization.web.director.dto.response.DirectorMatchResponse;

/**
 * Copas del director (ms-competitions): competencia, nomina y programacion de la llave. Los resultados no pasan
 * por aqui: los carga el delegado asignado. Los nombres de deporte e institucion salen del snapshot de
 * ms-institutions (en null si no responde); los de los equipos, de la nomina de la competencia.
 */
@Service
public class DirectorCompetitionService {

	private static final String IN_PROGRESS = "IN_PROGRESS";

	private final CompetitionsClient competitionsClient;
	private final CatalogService catalogService;
	private final DirectorCompetitionMapper directorCompetitionMapper;

	public DirectorCompetitionService(CompetitionsClient competitionsClient, CatalogService catalogService,
			DirectorCompetitionMapper directorCompetitionMapper) {
		this.competitionsClient = competitionsClient;
		this.catalogService = catalogService;
		this.directorCompetitionMapper = directorCompetitionMapper;
	}

	/** Todas, incluidos los borradores */
	public List<DirectorCompetitionResponse> getCompetitions() {
		return directorCompetitionMapper.toCompetitionResponses(competitionsClient.getCompetitions(),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	public DirectorCompetitionResponse getCompetition(Long id) {
		return directorCompetitionMapper.toCompetitionResponse(competitionsClient.getCompetition(id),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	/** Crea la copa en DRAFT junto con su llave vacia de 15 partidos */
	public DirectorCompetitionResponse createCompetition(DirectorCompetitionRequest request) {
		return directorCompetitionMapper.toCompetitionResponse(
				competitionsClient.createCompetition(directorCompetitionMapper.toCompetitionRequest(request)),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	public DirectorCompetitionResponse updateCompetition(Long id, DirectorCompetitionRequest request) {
		return directorCompetitionMapper.toCompetitionResponse(
				competitionsClient.updateCompetition(id, directorCompetitionMapper.toCompetitionRequest(request)),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	/** Inicia la copa: exige 16 equipos y los octavos sorteados, y publica sus partidos en el calendario */
	public DirectorCompetitionResponse startCompetition(Long id) {
		return directorCompetitionMapper.toCompetitionResponse(
				competitionsClient.changeStatus(id, new CompetitionStatusRequest(IN_PROGRESS)),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	public void deleteCompetition(Long id) {
		competitionsClient.deleteCompetition(id);
	}

	public List<CompetitionTeamResponse> getTeams(Long id) {
		return directorCompetitionMapper.toTeamResponses(competitionsClient.getTeams(id),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	public CompetitionTeamResponse enrollTeam(Long id, CompetitionTeamRequest request) {
		return directorCompetitionMapper.toTeamResponse(
				competitionsClient.enrollTeam(id, directorCompetitionMapper.toEnrollmentRequest(request)),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	public void unenrollTeam(Long id, Long teamId) {
		competitionsClient.unenrollTeam(id, teamId);
	}

	public List<DirectorMatchResponse> getMatches(Long id) {
		return directorCompetitionMapper.toMatchResponses(competitionsClient.getMatches(id),
				competitionsClient.getTeams(id));
	}

	public DirectorMatchResponse updateMatch(Long id, Long matchId, DirectorMatchRequest request) {
		MatchResponse updated = competitionsClient.updateMatch(id, matchId,
				directorCompetitionMapper.toMatchUpdateRequest(request));
		return directorCompetitionMapper.toMatchResponse(updated, competitionsClient.getTeams(id));
	}

}
