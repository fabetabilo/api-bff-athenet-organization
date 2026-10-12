package com.athenet.bff_organization.web.director;

import java.net.URI;
import java.util.List;

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

import com.athenet.bff_organization.service.director.DirectorEventService;
import com.athenet.bff_organization.web.director.dto.request.DirectorEventRequest;
import com.athenet.bff_organization.web.director.dto.response.DirectorEventResponse;
import com.athenet.bff_organization.web.director.dto.response.EventTypeOptionResponse;

import lombok.RequiredArgsConstructor;

/** CRUD de eventos del director (rol DIRECTOR, ver SecurityConfig) */
@RestController
@RequestMapping("/api/director")
@RequiredArgsConstructor
public class DirectorEventController {

	private final DirectorEventService directorEventService;

	/** Todos los eventos, en cualquier estado */
	@GetMapping("/events")
	public List<DirectorEventResponse> getEvents() {
		return directorEventService.getEvents();
	}

	@GetMapping("/events/{internalId}")
	public DirectorEventResponse getEvent(@PathVariable String internalId) {
		return directorEventService.getEvent(internalId);
	}

	@PostMapping("/events")
	public ResponseEntity<DirectorEventResponse> createEvent(@RequestBody DirectorEventRequest request) {
		DirectorEventResponse created = directorEventService.createEvent(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{internalId}")
				.buildAndExpand(created.internalId())
				.toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PutMapping("/events/{internalId}")
	public DirectorEventResponse updateEvent(@PathVariable String internalId,
			@RequestBody DirectorEventRequest request) {
		return directorEventService.updateEvent(internalId, request);
	}

	@DeleteMapping("/events/{internalId}")
	public ResponseEntity<Void> deleteEvent(@PathVariable String internalId) {
		directorEventService.deleteEvent(internalId);
		return ResponseEntity.noContent().build();
	}

	/** Dropdown "Tipo de evento" */
	@GetMapping("/event-types")
	public List<EventTypeOptionResponse> getEventTypes() {
		return directorEventService.getEventTypes();
	}

}
