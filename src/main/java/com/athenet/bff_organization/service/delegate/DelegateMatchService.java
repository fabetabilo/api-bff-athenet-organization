package com.athenet.bff_organization.service.delegate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.athenet.bff_organization.client.competitions.CompetitionsClient;
import com.athenet.bff_organization.client.competitions.dto.response.CompetitionResponse;
import com.athenet.bff_organization.client.competitions.dto.response.MatchResponse;
import com.athenet.bff_organization.service.common.CatalogService;
import com.athenet.bff_organization.service.exception.ServiceException;
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
		var competitions = competitionsById();
		var refs = catalogService.getInstitutionsSnapshotOrEmpty();
		return delegateMatchMapper.toDelegateMatchResponse(competitionsClient.startMatch(matchId), competitions, refs);
	}

	/** Finaliza el partido (el ganador avanza en la llave) o corrige su resultado */
	public DelegateMatchResponse registerResult(Long matchId, DelegateResultRequest request) {
		var competitions = competitionsById();
		var refs = catalogService.getInstitutionsSnapshotOrEmpty();
		return delegateMatchMapper.toDelegateMatchResponse(
				competitionsClient.registerResult(matchId, delegateMatchMapper.toMatchResultRequest(request)), competitions, refs);
	}

	private Map<Long, CompetitionResponse> competitionsById() {
		try {
			return competitionsClient.getPublicCompetitions().stream()
					.collect(Collectors.toMap(CompetitionResponse::id, Function.identity()));
		} catch (ResourceAccessException ex) {
			return Map.of();
		} catch (ServiceException ex) {
			if (ex.getType() != ServiceException.Type.SERVICE_UNAVAILABLE
					&& ex.getType() != ServiceException.Type.UPSTREAM_ERROR) throw ex;
			return Map.of();
		}
	}

}
