package com.ttech.orch.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ttech.orch.controller.EventController;
import com.ttech.orch.model.Event;
import com.ttech.orch.service.EventService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class EventServiceImpl implements EventService {
	
		
	@Autowired
	ActorServiceImpl actorServiceImpl;
	
	@Autowired
	@Qualifier("kafkaRestTemplate")
	private RestTemplate kafkaRestTemplate;

	@CircuitBreaker(name = "events_byActorId_repoId")
	public ResponseEntity<List<Event>> getEventsByActorIdRepoId(Long repoID,  Long actorID){
		ResponseEntity<List<Event>> response = null;
		try {
			response = kafkaRestTemplate.exchange("http://localhost:9999/events/repos/123/actors/123", HttpMethod.GET,HttpEntity.EMPTY, new ParameterizedTypeReference<List<Event>>() {
			});
		}catch(HttpClientErrorException e) {
			e.printStackTrace();
		}
		
		return response;
	}
}
