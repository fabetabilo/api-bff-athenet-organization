package com.athenet.bff_organization.service.director;

import java.util.List;

import org.springframework.stereotype.Component;

import com.athenet.bff_organization.client.events.dto.request.EventRequest;
import com.athenet.bff_organization.client.events.dto.response.EventResponse;
import com.athenet.bff_organization.client.events.dto.response.EventTypeResponse;
import com.athenet.bff_organization.client.institutions.dto.response.DeporteResponse;
import com.athenet.bff_organization.client.institutions.dto.response.EquipoResponse;
import com.athenet.bff_organization.client.institutions.dto.response.InstitucionResponse;
import com.athenet.bff_organization.client.institutions.dto.response.SedeResponse;
import com.athenet.bff_organization.service.common.InstitutionsSnapshot;
import com.athenet.bff_organization.web.director.dto.request.DirectorEventRequest;
import com.athenet.bff_organization.web.director.dto.response.DirectorEventResponse;
import com.athenet.bff_organization.web.director.dto.response.EventTypeOptionResponse;

/**
 * Lectura: EventResponse (plano, con IDs) + InstitutionsSnapshot → DirectorEventResponse (referencias resueltas,
 * sin id de BD). Escritura: DirectorEventRequest → EventRequest.
 */
@Component
public class DirectorEventMapper {

	public List<DirectorEventResponse> toDirectorEventResponses(List<EventResponse> events, InstitutionsSnapshot refs) {
		return events.stream().map(e -> toDirectorEventResponse(e, refs)).toList();
	}

	public DirectorEventResponse toDirectorEventResponse(EventResponse e, InstitutionsSnapshot refs) {
		return new DirectorEventResponse(
				e.internalId(),
				e.title(),
				e.description(),
				e.descriptionOpt(),
				e.type(),
				e.status(),
				e.isOfficial(),
				e.eventDate(),
				e.startTime(),
				e.endDate(),
				e.endTime(),
				e.location(),
				e.address(),
				e.coverImage(),
				e.photos(),
				sport(refs, e.sportId()),
				organizer(refs, e.organizerInstitutionId(), e.organizerCampusId(), e.organizerTeamId()),
				matchTeam(refs, e.teamOneId()),
				matchTeam(refs, e.teamTwoId()),
				e.delegateOid() != null ? new DirectorEventResponse.Delegate(e.delegateOid(), e.delegateDisplayName()) : null);
	}

	/** No reenvia teamXInstitutionId ni teamXCampusId (solo sirven para validar) */
	public EventRequest toEventRequest(DirectorEventRequest r) {
		return new EventRequest(
				r.internalId(),
				r.title(),
				r.description(),
				r.descriptionOpt(),
				r.type(),
				r.status(),
				r.isOfficial(),
				r.eventDate(),
				r.startTime(),
				r.endDate(),
				r.endTime(),
				r.location(),
				r.address(),
				r.coverImage(),
				r.photos(),
				r.sportId(),
				r.organizerInstitutionId(),
				r.organizerCampusId(),
				r.organizerTeamId(),
				r.teamOneId(),
				r.teamTwoId(),
				r.delegateOid(),
				r.delegateDisplayName());
	}

	public List<EventTypeOptionResponse> toEventTypeOptions(List<EventTypeResponse> types) {
		return types.stream()
				.map(t -> new EventTypeOptionResponse(t.code(), t.label(), t.requiresMatchup()))
				.toList();
	}

	// Referencias de un evento: si el id ya no existe en ms-institutions, se devuelve con name en null

	private DirectorEventResponse.Sport sport(InstitutionsSnapshot refs, Long id) {
		if (id == null) return null;
		DeporteResponse deporte = refs.deporte(id);
		return new DirectorEventResponse.Sport(id, deporte != null ? deporte.nombre() : null);
	}

	private DirectorEventResponse.Organizer organizer(InstitutionsSnapshot refs, Long institutionId, Long campusId,
			Long teamId) {
		if (institutionId == null && campusId == null && teamId == null) return null;
		return new DirectorEventResponse.Organizer(institution(refs, institutionId), campus(refs, campusId),
				team(refs, teamId));
	}

	/** La institucion sale de la sede del equipo: EquipoResponse no trae institucionId ni sigla */
	private DirectorEventResponse.MatchTeam matchTeam(InstitutionsSnapshot refs, Long id) {
		if (id == null) return null;
		EquipoResponse equipo = refs.equipo(id);
		if (equipo == null) return new DirectorEventResponse.MatchTeam(id, null, null, null, null);
		SedeResponse sede = refs.sede(equipo.sedeId());
		return new DirectorEventResponse.MatchTeam(id, equipo.nombre(), equipo.categoria(),
				campus(refs, equipo.sedeId()),
				institution(refs, sede != null ? sede.institucionId() : null));
	}

	private DirectorEventResponse.Institution institution(InstitutionsSnapshot refs, Long id) {
		if (id == null) return null;
		InstitucionResponse institucion = refs.institucion(id);
		return institucion != null
				? new DirectorEventResponse.Institution(id, institucion.nombre(), institucion.sigla())
				: new DirectorEventResponse.Institution(id, null, null);
	}

	private DirectorEventResponse.Campus campus(InstitutionsSnapshot refs, Long id) {
		if (id == null) return null;
		SedeResponse sede = refs.sede(id);
		return new DirectorEventResponse.Campus(id, sede != null ? sede.nombre() : null);
	}

	private DirectorEventResponse.Team team(InstitutionsSnapshot refs, Long id) {
		if (id == null) return null;
		EquipoResponse equipo = refs.equipo(id);
		return equipo != null
				? new DirectorEventResponse.Team(id, equipo.nombre(), equipo.categoria())
				: new DirectorEventResponse.Team(id, null, null);
	}

}
