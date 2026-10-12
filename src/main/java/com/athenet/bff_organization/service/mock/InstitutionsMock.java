package com.athenet.bff_organization.service.mock;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.athenet.bff_organization.web.common.dto.response.CampusOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.InstitutionOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.SportOptionResponse;
import com.athenet.bff_organization.web.common.dto.response.TeamOptionResponse;
import com.athenet.bff_organization.web.director.dto.response.DirectorEventResponse;

/**
 * MOCK de ms-institutions: datos en memoria con los mismos IDs que los eventos de prueba de ms-events.
 * Lo usan CatalogService y DirectorEventMapper; se elimina al integrar ms-institutions.
 */
@Component
public class InstitutionsMock {

	private record Sport(Long id, String name) {}

	private record Institution(Long id, String name, String acronym) {}

	private record Campus(Long id, Long institutionId, String name) {}

	private record Team(Long id, Long campusId, Long sportId, String name, String division) {}

	private static final List<Sport> SPORTS = List.of(
			new Sport(1L, "Básquetbol"),
			new Sport(2L, "Vóleibol"),
			new Sport(3L, "Fútbol"));

	private static final List<Institution> INSTITUTIONS = List.of(
			new Institution(1L, "Universidad de Santiago de Chile", "USACH"),
			new Institution(2L, "Universidad de Chile", "UCH"));

	private static final List<Campus> CAMPUSES = List.of(
			new Campus(10L, 1L, "Casa Central"),
			new Campus(11L, 1L, "Sede Maipú"),
			new Campus(20L, 2L, "Campus Deportivo"));

	private static final List<Team> TEAMS = List.of(
			new Team(100L, 10L, 3L, "USACH Fútbol Varones", "MASCULINO"),
			new Team(103L, 10L, 1L, "USACH Básquetbol Damas", "FEMENINO"),
			new Team(101L, 11L, 3L, "USACH Maipú Fútbol Varones", "MASCULINO"),
			new Team(102L, 11L, 3L, "USACH Maipú Fútbol Varones B", "MASCULINO"),
			new Team(200L, 20L, 3L, "UCH Fútbol Varones", "MASCULINO"),
			new Team(201L, 20L, 3L, "UCH Fútbol Varones B", "MASCULINO"),
			new Team(202L, 20L, 1L, "UCH Básquetbol Varones", "MASCULINO"));

	// Dropdowns: ordenados por nombre; sportId y division son opcionales (null = sin filtro)

	public List<SportOptionResponse> getSports() {
		return SPORTS.stream()
				.sorted(Comparator.comparing(Sport::name))
				.map(s -> new SportOptionResponse(s.id(), s.name()))
				.toList();
	}

	public List<InstitutionOptionResponse> getInstitutions(Long sportId) {
		Set<Long> campusIds = campusIdsWithSport(sportId);
		Set<Long> institutionIds = CAMPUSES.stream()
				.filter(c -> campusIds.contains(c.id()))
				.map(Campus::institutionId)
				.collect(Collectors.toSet());
		return INSTITUTIONS.stream()
				.filter(i -> institutionIds.contains(i.id()))
				.sorted(Comparator.comparing(Institution::name))
				.map(i -> new InstitutionOptionResponse(i.id(), i.name(), i.acronym()))
				.toList();
	}

	public List<CampusOptionResponse> getCampuses(Long institutionId, Long sportId) {
		Set<Long> campusIds = campusIdsWithSport(sportId);
		return CAMPUSES.stream()
				.filter(c -> c.institutionId().equals(institutionId) && campusIds.contains(c.id()))
				.sorted(Comparator.comparing(Campus::name))
				.map(c -> new CampusOptionResponse(c.id(), c.name()))
				.toList();
	}

	public List<TeamOptionResponse> getTeams(Long campusId, Long sportId, String division) {
		return TEAMS.stream()
				.filter(t -> t.campusId().equals(campusId))
				.filter(t -> sportId == null || t.sportId().equals(sportId))
				.filter(t -> division == null || t.division().equalsIgnoreCase(division))
				.sorted(Comparator.comparing(Team::name))
				.map(t -> new TeamOptionResponse(t.id(), t.name(), t.division()))
				.toList();
	}

	// Referencias de un evento: si el id no existe, se devuelve con name en null

	public DirectorEventResponse.Sport sport(Long id) {
		if (id == null) return null;
		Sport sport = SPORTS.stream().filter(s -> s.id().equals(id)).findFirst().orElse(null);
		return new DirectorEventResponse.Sport(id, sport != null ? sport.name() : null);
	}

	public DirectorEventResponse.Organizer organizer(Long institutionId, Long campusId, Long teamId) {
		if (institutionId == null && campusId == null && teamId == null) return null;
		return new DirectorEventResponse.Organizer(institution(institutionId), campus(campusId), team(teamId));
	}

	public DirectorEventResponse.MatchTeam matchTeam(Long id) {
		if (id == null) return null;
		Team team = findTeam(id);
		if (team == null) return new DirectorEventResponse.MatchTeam(id, null, null, null, null);
		Campus campus = findCampus(team.campusId());
		return new DirectorEventResponse.MatchTeam(id, team.name(), team.division(),
				campus(team.campusId()),
				institution(campus != null ? campus.institutionId() : null));
	}

	private DirectorEventResponse.Institution institution(Long id) {
		if (id == null) return null;
		Institution institution = INSTITUTIONS.stream().filter(i -> i.id().equals(id)).findFirst().orElse(null);
		return institution != null
				? new DirectorEventResponse.Institution(id, institution.name(), institution.acronym())
				: new DirectorEventResponse.Institution(id, null, null);
	}

	private DirectorEventResponse.Campus campus(Long id) {
		if (id == null) return null;
		Campus campus = findCampus(id);
		return new DirectorEventResponse.Campus(id, campus != null ? campus.name() : null);
	}

	private DirectorEventResponse.Team team(Long id) {
		if (id == null) return null;
		Team team = findTeam(id);
		return team != null ? new DirectorEventResponse.Team(id, team.name(), team.division()) : new DirectorEventResponse.Team(id, null, null);
	}

	private Campus findCampus(Long id) {
		return CAMPUSES.stream().filter(c -> c.id().equals(id)).findFirst().orElse(null);
	}

	private Team findTeam(Long id) {
		return TEAMS.stream().filter(t -> t.id().equals(id)).findFirst().orElse(null);
	}

	private Set<Long> campusIdsWithSport(Long sportId) {
		return (sportId == null
				? CAMPUSES.stream().map(Campus::id)
				: TEAMS.stream().filter(t -> t.sportId().equals(sportId)).map(Team::campusId))
				.collect(Collectors.toSet());
	}

}
