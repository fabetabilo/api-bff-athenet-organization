package com.athenet.bff_organization.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.athenet.bff_organization.service.PublicEventsService;
import com.athenet.bff_organization.web.dto.response.PublicEventResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/events")
@RequiredArgsConstructor
public class PublicEventsController {

	private final PublicEventsService publicEventsService;

	@GetMapping
	public List<PublicEventResponse> getPublicEvents() {
		return publicEventsService.getPublicEvents();
	}

}
