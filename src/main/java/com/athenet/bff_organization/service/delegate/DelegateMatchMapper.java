package com.athenet.bff_organization.service.delegate;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.athenet.bff_organization.client.competitions.dto.request.MatchResultRequest;
import com.athenet.bff_organization.client.competitions.dto.response.CompetitionResponse;
import com.athenet.bff_organization.client.competitions.dto.response.MatchResponse;
import com.athenet.bff_organization.client.institutions.dto.response.EquipoResponse;
import com.athenet.bff_organization.service.common.InstitutionsSnapshot;
import com.athenet.bff_organization.service.common.MatchRoundLabels;
import com.athenet.bff_organization.web.delegate.dto.request.DelegateResultRequest;
import com.athenet.bff_organization.web.delegate.dto.response.DelegateMatchResponse;

/** MatchResponse + competencias + snapshot de ms-institutions → DelegateMatchResponse */
@Component
public class DelegateMatchMapper {

	public MatchResultRequest toMatchResultRequest(DelegateResultRequest r) {
		return new MatchResultRequest(r.homeScore(), r.awayScore());
	}

	public List<DelegateMatchResponse> toDelegateMatchResponses(List<MatchResponse> matches,
			Map<Long, CompetitionResponse> competitions, InstitutionsSnapshot refs) {
		return matches.stream().map(m -> toDelegateMatchResponse(m, competitions, refs)).toList();
	}

	public DelegateMatchResponse toDelegateMatchResponse(MatchResponse m, Map<Long, CompetitionResponse> competitions,
			InstitutionsSnapshot refs) {
		CompetitionResponse competition = competitions.get(m.competitionId());
		return new DelegateMatchResponse(
				m.id(),
				competition != null
						? new DelegateMatchResponse.Competition(m.competitionId(), competition.name(), competition.competitionImg())
						: new DelegateMatchResponse.Competition(m.competitionId(), null, null),
				m.round(),
				MatchRoundLabels.of(m.round()),
				m.date(),
				m.time(),
				m.location(),
				m.address(),
				team(refs, m.homeTeamId()),
				team(refs, m.awayTeamId()),
				m.status(),
				m.homeScore(),
				m.awayScore(), m.endDate(), m.endTime(), Boolean.TRUE.equals(m.scheduleConfirmed()), m.scheduleVersion() == null ? 0 : m.scheduleVersion(), m.version());
	}

	private static DelegateMatchResponse.Team team(InstitutionsSnapshot refs, Long id) {
		if (id == null) return null;
		EquipoResponse equipo = refs.equipo(id);
		return equipo != null
				? new DelegateMatchResponse.Team(id, equipo.nombre(), equipo.logoUrl())
				: new DelegateMatchResponse.Team(id, null, null);
	}

}
