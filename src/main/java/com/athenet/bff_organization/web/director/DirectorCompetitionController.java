package com.athenet.bff_organization.web.director;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.athenet.bff_organization.service.director.DirectorCompetitionService;
import com.athenet.bff_organization.web.director.dto.request.CompetitionTeamRequest;
import com.athenet.bff_organization.web.director.dto.request.DirectorCompetitionRequest;
import com.athenet.bff_organization.web.director.dto.request.DirectorMatchRequest;
import com.athenet.bff_organization.web.director.dto.response.CompetitionTeamResponse;
import com.athenet.bff_organization.web.director.dto.response.DirectorCompetitionResponse;
import com.athenet.bff_organization.web.director.dto.response.DirectorMatchResponse;

/** Copas del director: competencias, nomina y encuentros (rol DIRECTOR, ver SecurityConfig) */
@RestController
@RequestMapping("/api/director/competitions")
public class DirectorCompetitionController {

	private final DirectorCompetitionService directorCompetitionService;

	public DirectorCompetitionController(DirectorCompetitionService directorCompetitionService) {
		this.directorCompetitionService = directorCompetitionService;
	}

	/** Tabla de competencias, en cualquier estado */
	@GetMapping
	public List<DirectorCompetitionResponse> getCompetitions() {
		return directorCompetitionService.getCompetitions();
	}

	@GetMapping("/{id}")
	public DirectorCompetitionResponse getCompetition(@PathVariable Long id) {
		return directorCompetitionService.getCompetition(id);
	}

	@PostMapping
	public ResponseEntity<DirectorCompetitionResponse> createCompetition(
			@RequestBody DirectorCompetitionRequest request) {
		DirectorCompetitionResponse created = directorCompetitionService.createCompetition(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(created.id())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/{id}")
	public DirectorCompetitionResponse updateCompetition(@PathVariable Long id,
			@RequestBody DirectorCompetitionRequest request) {
		return directorCompetitionService.updateCompetition(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCompetition(@PathVariable Long id) {
		directorCompetitionService.deleteCompetition(id);
		return ResponseEntity.noContent().build();
	}

	/** Boton "Iniciar copa" (DRAFT → IN_PROGRESS) */
	@PostMapping("/{id}/start")
	public DirectorCompetitionResponse startCompetition(@PathVariable Long id) {
		return directorCompetitionService.startCompetition(id);
	}

	/** Pestaña "Equipos" */
	@GetMapping("/{id}/teams")
	public List<CompetitionTeamResponse> getTeams(@PathVariable Long id) {
		return directorCompetitionService.getTeams(id);
	}

	@PostMapping("/{id}/teams")
	public ResponseEntity<CompetitionTeamResponse> enrollTeam(@PathVariable Long id,
			@RequestBody CompetitionTeamRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(directorCompetitionService.enrollTeam(id, request));
	}

	@DeleteMapping("/{id}/teams/{teamId}")
	public ResponseEntity<Void> unenrollTeam(@PathVariable Long id, @PathVariable Long teamId) {
		directorCompetitionService.unenrollTeam(id, teamId);
		return ResponseEntity.noContent().build();
	}

	/** Pestaña "Encuentros": la llave ordenada por ronda y posicion */
	@GetMapping("/{id}/matches")
	public List<DirectorMatchResponse> getMatches(@PathVariable Long id) {
		return directorCompetitionService.getMatches(id);
	}

	@PutMapping("/{id}/matches/{matchId}")
	public DirectorMatchResponse updateMatch(@PathVariable Long id, @PathVariable Long matchId,
			@RequestBody DirectorMatchRequest request) {
		return directorCompetitionService.updateMatch(id, matchId, request);
	}

	@PostMapping("/{id}/matches/{matchId}/confirm")
	public DirectorMatchResponse confirmMatch(@PathVariable Long id, @PathVariable Long matchId) {
		return directorCompetitionService.confirmMatch(id, matchId);
	}

}
