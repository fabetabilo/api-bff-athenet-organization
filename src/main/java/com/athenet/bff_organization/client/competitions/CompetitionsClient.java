package com.athenet.bff_organization.client.competitions;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.athenet.bff_organization.client.competitions.dto.request.CompetitionRequest;
import com.athenet.bff_organization.client.competitions.dto.request.CompetitionStatusRequest;
import com.athenet.bff_organization.client.competitions.dto.request.EnrollmentRequest;
import com.athenet.bff_organization.client.competitions.dto.request.MatchResultRequest;
import com.athenet.bff_organization.client.competitions.dto.request.MatchUpdateRequest;
import com.athenet.bff_organization.client.competitions.dto.response.CompetitionResponse;
import com.athenet.bff_organization.client.competitions.dto.response.EnrollmentResponse;
import com.athenet.bff_organization.client.competitions.dto.response.MatchResponse;

/** ms-competitions: /api/admin/** requiere rol DIRECTOR y /api/delegate/** rol DELEGATE (el token se reenvia) */
@Component
public class CompetitionsClient {

	private final RestClient restClient;

	public CompetitionsClient(@Qualifier("competitionsRestClient") RestClient restClient) {
		this.restClient = restClient;
	}

	// --- Director -----------------------------------------------------------

	/** Todas las competencias, incluidos los borradores */
	public List<CompetitionResponse> getCompetitions() {
		return restClient.get()
				.uri("/api/admin/competitions")
				.retrieve()
				.body(new ParameterizedTypeReference<List<CompetitionResponse>>() {});
	}

	public CompetitionResponse getCompetition(Long id) {
		return restClient.get()
				.uri("/api/admin/competitions/{id}", id)
				.retrieve()
				.body(CompetitionResponse.class);
	}

	/** Crea la copa y su llave vacia (201) */
	public CompetitionResponse createCompetition(CompetitionRequest request) {
		return restClient.post()
				.uri("/api/admin/competitions")
				.body(request)
				.retrieve()
				.body(CompetitionResponse.class);
	}

	public CompetitionResponse updateCompetition(Long id, CompetitionRequest request) {
		return restClient.put()
				.uri("/api/admin/competitions/{id}", id)
				.body(request)
				.retrieve()
				.body(CompetitionResponse.class);
	}

	public CompetitionResponse changeStatus(Long id, CompetitionStatusRequest request) {
		return restClient.patch()
				.uri("/api/admin/competitions/{id}/status", id)
				.body(request)
				.retrieve()
				.body(CompetitionResponse.class);
	}

	/** Solo en DRAFT (204) */
	public void deleteCompetition(Long id) {
		restClient.delete()
				.uri("/api/admin/competitions/{id}", id)
				.retrieve()
				.toBodilessEntity();
	}

	public List<EnrollmentResponse> getTeams(Long id) {
		return restClient.get()
				.uri("/api/admin/competitions/{id}/teams", id)
				.retrieve()
				.body(new ParameterizedTypeReference<List<EnrollmentResponse>>() {});
	}

	/** 201 */
	public EnrollmentResponse enrollTeam(Long id, EnrollmentRequest request) {
		return restClient.post()
				.uri("/api/admin/competitions/{id}/teams", id)
				.body(request)
				.retrieve()
				.body(EnrollmentResponse.class);
	}

	/** 204 */
	public void unenrollTeam(Long id, Long teamId) {
		restClient.delete()
				.uri("/api/admin/competitions/{id}/teams/{teamId}", id, teamId)
				.retrieve()
				.toBodilessEntity();
	}

	/** Llave ordenada por ronda y posicion */
	public List<MatchResponse> getMatches(Long id) {
		return restClient.get()
				.uri("/api/admin/competitions/{id}/matches", id)
				.retrieve()
				.body(new ParameterizedTypeReference<List<MatchResponse>>() {});
	}

	public MatchResponse updateMatch(Long id, Long matchId, MatchUpdateRequest request) {
		return restClient.put()
				.uri("/api/admin/competitions/{id}/matches/{matchId}", id, matchId)
				.body(request)
				.retrieve()
				.body(MatchResponse.class);
	}

    public MatchResponse confirmMatch(Long id, Long matchId) {
        return restClient.post().uri("/api/admin/competitions/{id}/matches/{matchId}/confirm", id, matchId)
                .retrieve().body(MatchResponse.class);
    }

	// --- Publico ------------------------------------------------------------

	/** Competencias sin borradores */
	public List<CompetitionResponse> getPublicCompetitions() {
		return restClient.get()
				.uri("/api/public/competitions")
				.retrieve()
				.body(new ParameterizedTypeReference<List<CompetitionResponse>>() {});
	}

	// --- Delegado -----------------------------------------------------------

	/** Partidos asignados al delegado del token, en competencias ya iniciadas */
	public List<MatchResponse> getDelegateMatches() {
		return restClient.get()
				.uri("/api/delegate/matches")
				.retrieve()
				.body(new ParameterizedTypeReference<List<MatchResponse>>() {});
	}

	public MatchResponse startMatch(Long matchId) {
		return restClient.post()
				.uri("/api/delegate/matches/{matchId}/start", matchId)
				.retrieve()
				.body(MatchResponse.class);
	}

	/** Finaliza el partido o corrige su resultado */
	public MatchResponse registerResult(Long matchId, MatchResultRequest request) {
		return restClient.put()
				.uri("/api/delegate/matches/{matchId}/result", matchId)
				.body(request)
				.retrieve()
				.body(MatchResponse.class);
	}

}
