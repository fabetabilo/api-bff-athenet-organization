package com.athenet.bff_organization.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.athenet.bff_organization.client.events.EventsClient;
import com.athenet.bff_organization.service.mapper.PublicEventMapper;
import com.athenet.bff_organization.web.dto.response.PublicEventResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicEventsService {

	private final EventsClient eventsClient;
	private final PublicEventMapper publicEventMapper;

	public List<PublicEventResponse> getPublicEvents() {
		return publicEventMapper.toPublicEventResponses(eventsClient.getPublicEvents());
	}

}
