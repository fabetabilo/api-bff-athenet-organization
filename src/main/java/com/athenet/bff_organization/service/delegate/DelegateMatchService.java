package com.athenet.bff_organization.service.delegate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.athenet.bff_organization.client.competitions.CompetitionsClient;
import com.athenet.bff_organization.client.competitions.dto.response.CompetitionResponse;
import com.athenet.bff_organization.client.competitions.dto.response.MatchResponse;
import com.athenet.bff_organization.service.common.CatalogService;
import com.athenet.bff_organization.web.delegate.dto.request.DelegateResultRequest;
import com.athenet.bff_organization.web.delegate.dto.response.DelegateMatchResponse;

/**
 * "Mis eventos" del delegado: sus partidos de ms-competitions. Que sea el delegado asignado lo valida
 * ms-competitions con el oid del token reenviado. Los nombres de equipo salen del snapshot de ms-institutions
 * (en null si no responde) y los de competencia, del listado publico.
 */
@Service
public class DelegateMatchService {

	private final CompetitionsClient competitionsClient;
	private final CatalogService catalogService;
	private final DelegateMatchMapper delegateMatchMapper;

	public DelegateMatchService(CompetitionsClient competitionsClient, CatalogService catalogService,
			DelegateMatchMapper delegateMatchMapper) {
		this.competitionsClient = competitionsClient;
		this.catalogService = catalogService;
		this.delegateMatchMapper = delegateMatchMapper;
	}

	public List<DelegateMatchResponse> getMatches() {
		List<MatchResponse> matches = competitionsClient.getDelegateMatches();
		if (matches.isEmpty()) return List.of();
		return delegateMatchMapper.toDelegateMatchResponses(matches, competitionsById(),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	/** Puede iniciarse antes de la hora programada */
	public DelegateMatchResponse startMatch(Long matchId) {
		return toResponse(competitionsClient.startMatch(matchId));
	}

	/** Finaliza el partido (el ganador avanza en la llave) o corrige su resultado */
	public DelegateMatchResponse registerResult(Long matchId, DelegateResultRequest request) {
		return toResponse(competitionsClient.registerResult(matchId, delegateMatchMapper.toMatchResultRequest(request)));
	}

	private DelegateMatchResponse toResponse(MatchResponse match) {
		return delegateMatchMapper.toDelegateMatchResponse(match, competitionsById(),
				catalogService.getInstitutionsSnapshotOrEmpty());
	}

	private Map<Long, CompetitionResponse> competitionsById() {
		return competitionsClient.getPublicCompetitions().stream()
				.collect(Collectors.toMap(CompetitionResponse::id, Function.identity()));
	}

}
