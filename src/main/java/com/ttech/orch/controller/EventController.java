package com.ttech.orch.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ttech.orch.model.Event;

@RestController
public class EventController {
	
	 @GetMapping(value = "/events/repos/{repoID}/actors/{actorID}")
	    @ResponseStatus(HttpStatus.OK)
	    public List<Event> getEventsByActorIdRepoId(@PathVariable Long repoID, @PathVariable Long actorID) {
	        //return repositoryService.getEventsByActorIdRepoId(repoID,actorID);
		return null;
	    }
}
