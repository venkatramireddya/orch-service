package com.ttech.orch.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ttech.orch.model.Event;

public interface EventService {
	ResponseEntity<List<Event>> getEventsByActorIdRepoId(Long repoID,  Long actorID);
	List<Event> getEventsByRepoId(Long repoID) throws Exception;
}
