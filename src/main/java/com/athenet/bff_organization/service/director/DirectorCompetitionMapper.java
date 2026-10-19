package com.athenet.bff_organization.service.director;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.athenet.bff_organization.client.competitions.dto.request.CompetitionRequest;
import com.athenet.bff_organization.client.competitions.dto.request.EnrollmentRequest;
import com.athenet.bff_organization.client.competitions.dto.request.MatchUpdateRequest;
import com.athenet.bff_organization.client.competitions.dto.response.CompetitionResponse;
import com.athenet.bff_organization.client.competitions.dto.response.EnrollmentResponse;
import com.athenet.bff_organization.client.competitions.dto.response.MatchResponse;
import com.athenet.bff_organization.client.institutions.dto.response.DeporteResponse;
import com.athenet.bff_organization.client.institutions.dto.response.InstitucionResponse;
import com.athenet.bff_organization.service.common.InstitutionsSnapshot;
import com.athenet.bff_organization.service.common.MatchRoundLabels;
import com.athenet.bff_organization.web.director.dto.request.CompetitionTeamRequest;
import com.athenet.bff_organization.web.director.dto.request.DirectorCompetitionRequest;
import com.athenet.bff_organization.web.director.dto.request.DirectorMatchRequest;
import com.athenet.bff_organization.web.director.dto.response.CompetitionTeamResponse;
import com.athenet.bff_organization.web.director.dto.response.DirectorCompetitionResponse;
import com.athenet.bff_organization.web.director.dto.response.DirectorMatchResponse;

/**
 * Competencias del director. Lectura: respuestas de ms-competitions + nombres (deporte e institucion del
 * snapshot de ms-institutions; equipos de la nomina de la competencia). Escritura: web.dto → client.dto.
 */
@Component
public class DirectorCompetitionMapper {

	// Por ahora toda competencia es una copa
	private static final String CUP = "CUP";

	/** Sin puntos: ms-competitions aplica los suyos por defecto */
	public CompetitionRequest toCompetitionRequest(DirectorCompetitionRequest r) {
		return new CompetitionRequest(
				r.name(),
				CUP,
				r.sportId(),
				r.season(),
				r.description(),
				r.competitionImg(),
				r.startDate(),
				r.endDate(),
				null,
				null,
				null);
	}

	public List<DirectorCompetitionResponse> toCompetitionResponses(List<CompetitionResponse> competitions,
			InstitutionsSnapshot refs) {
		return competitions.stream().map(c -> toCompetitionResponse(c, refs)).toList();
	}

	public DirectorCompetitionResponse toCompetitionResponse(CompetitionResponse c, InstitutionsSnapshot refs) {
		DeporteResponse deporte = refs.deporte(c.sportId());
		return new DirectorCompetitionResponse(
				c.id(),
				c.name(),
				c.type(),
				new DirectorCompetitionResponse.Sport(c.sportId(), deporte != null ? deporte.nombre() : null),
				c.season(),
				c.description(),
				c.competitionImg(),
				c.status(),
				c.startDate(),
				c.endDate());
	}

	public EnrollmentRequest toEnrollmentRequest(CompetitionTeamRequest r) {
		return new EnrollmentRequest(r.teamId());
	}

	public List<CompetitionTeamResponse> toTeamResponses(List<EnrollmentResponse> teams, InstitutionsSnapshot refs) {
		return teams.stream().map(t -> toTeamResponse(t, refs)).toList();
	}

	public CompetitionTeamResponse toTeamResponse(EnrollmentResponse e, InstitutionsSnapshot refs) {
		InstitucionResponse institucion = refs.institucion(e.institutionId());
		return new CompetitionTeamResponse(
				e.teamId(),
				e.teamName(),
				e.teamLogoUrl(),
				institucion != null
						? new CompetitionTeamResponse.Institution(e.institutionId(), institucion.nombre(), institucion.sigla())
						: new CompetitionTeamResponse.Institution(e.institutionId(), null, null));
	}

	public MatchUpdateRequest toMatchUpdateRequest(DirectorMatchRequest r) {
		return new MatchUpdateRequest(
				r.date(),
				r.time(),
				r.location(),
				r.address(),
				r.homeTeamId(),
				r.awayTeamId(),
				r.delegateOid(),
				r.delegateDisplayName(), r.endDate(), r.endTime());
	}

	public List<DirectorMatchResponse> toMatchResponses(List<MatchResponse> matches, List<EnrollmentResponse> teams) {
		Map<Long, EnrollmentResponse> teamsById = byTeamId(teams);
		return matches.stream().map(m -> toMatchResponse(m, teamsById)).toList();
	}

	public DirectorMatchResponse toMatchResponse(MatchResponse m, List<EnrollmentResponse> teams) {
		return toMatchResponse(m, byTeamId(teams));
	}

	private DirectorMatchResponse toMatchResponse(MatchResponse m, Map<Long, EnrollmentResponse> teamsById) {
		return new DirectorMatchResponse(
				m.id(),
				m.round(),
				MatchRoundLabels.of(m.round()),
				m.position(),
				m.date(),
				m.time(),
				m.location(),
				m.address(),
				team(teamsById, m.homeTeamId()),
				team(teamsById, m.awayTeamId()),
				m.delegateOid() != null ? new DirectorMatchResponse.Delegate(m.delegateOid(), m.delegateDisplayName()) : null,
				m.status(),
				m.homeScore(),
				m.awayScore(), m.endDate(), m.endTime(), Boolean.TRUE.equals(m.scheduleConfirmed()), m.scheduleVersion() == null ? 0 : m.scheduleVersion());
	}

	private static DirectorMatchResponse.Team team(Map<Long, EnrollmentResponse> teamsById, Long id) {
		if (id == null) return null;
		EnrollmentResponse team = teamsById.get(id);
		return team != null
				? new DirectorMatchResponse.Team(id, team.teamName(), team.teamLogoUrl())
				: new DirectorMatchResponse.Team(id, null, null);
	}

	private static Map<Long, EnrollmentResponse> byTeamId(List<EnrollmentResponse> teams) {
		return teams.stream().collect(Collectors.toMap(EnrollmentResponse::teamId, Function.identity()));
	}

}
