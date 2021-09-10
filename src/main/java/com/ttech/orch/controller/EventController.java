package com.ttech.orch.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttech.orch.model.Event;
import com.ttech.orch.model.Message;
import com.ttech.orch.service.EventService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/event-gateway")
public class EventController {
	
	@Autowired
	EventService eventService;
	
	
	//@RolesAllowed("")
	//@PreAuthorize("hasRole('ROLE_USER')")
	@GetMapping(value = "/events/repos/{repoID}")
    public List<Event> getEventsByRepoId(@PathVariable Long repoID) throws Exception {
        return eventService.getEventsByRepoId(repoID);
    }
	
	
	@GetMapping(value = "/events/repos/{repoID}/actors/{actorID}")
	public ResponseEntity<List<Event>> getEventsByActorIdRepoId(@PathVariable Long repoID, @PathVariable Long actorID) {
        return eventService.getEventsByActorIdRepoId(repoID,actorID);
    }
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@PostMapping(value = "/events")
	public ResponseEntity<List<Message>> saveEvents(@RequestBody Message message) {
		log.info("Enter in to Method:{}" );
		return null;
        //return eventService.getEventsByActorIdRepoId(message);
    }	
}
