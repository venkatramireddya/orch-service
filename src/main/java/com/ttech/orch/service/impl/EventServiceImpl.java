package com.ttech.orch.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ttech.orch.model.Event;
import com.ttech.orch.service.EventService;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired
	@Qualifier("kafkaRestTemplate")
	private RestTemplate kafkaRestTemplate;

	public ResponseEntity<List<Event>> getEventsByActorIdRepoId(Long repoID,  Long actorID){
		//kafkaRestTemplate.
		
		return null;
	}
}
