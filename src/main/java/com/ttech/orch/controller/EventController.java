package com.ttech.orch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ttech.orch.model.Event;
import com.ttech.orch.service.EventService;

@RestController
public class EventController {
	
	@Autowired
	EventService eventService;
	
	@GetMapping(value = "/events/repos/{repoID}/actors/{actorID}")
	public ResponseEntity<List<Event>> getEventsByActorIdRepoId(@PathVariable Long repoID, @PathVariable Long actorID) {
        return eventService.getEventsByActorIdRepoId(repoID,actorID);
	//return null;
    }	
	 	
}
