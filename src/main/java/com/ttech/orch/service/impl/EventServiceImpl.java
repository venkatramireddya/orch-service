package com.ttech.orch.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.spi.impl.InternalCompletableFuture;
import com.ttech.orch.model.Event;
import com.ttech.orch.service.EventService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
	
		
	@Autowired
	ActorServiceImpl actorServiceImpl;
	
	@Autowired
	@Qualifier("kafkaRestTemplate")
	private RestTemplate kafkaRestTemplate;
	
	@Autowired
	HazelcastInstance hazelcastInstance;

	@Value("${hazelcast.map.name: orchMap}")
	@Setter
	private String mapName;
	
	@Value("${hazelcast.read.timeout.msec:100}")
	private long hazelcastReadTimeOut;
	
	
	@CircuitBreaker(name = "events_byActorId_repoId")
	@Retry(name ="retry")
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

	 public List<Event> getEventsByRepoId(Long repoID) throws Exception {
		 
		 List<Event> value = readFromCache(repoID);
		 if (value == null) {
			 ResponseEntity<List<Event>> response = null;
				try {
					response = kafkaRestTemplate.exchange("http://localhost:9999/events/repos/123/actors/123", HttpMethod.GET,HttpEntity.EMPTY, new ParameterizedTypeReference<List<Event>>() {
					});
				}catch(HttpClientErrorException e) {
					e.printStackTrace();
				}
				value = response.getBody();
			 writeDateToCache(repoID,  value ); 
		 }
		 return value;
	 }
	 
	 private void writeDateToCache(Long repoID, List<Event> value) {
		
		 if(value != null) {
			 try {
				 final IMap<Long, List<Event>> hazelcastMap = hazelcastInstance.getMap(mapName);
				 hazelcastMap.setAsync(repoID, value);
				 log.info("Write to cache account for accId {}", repoID);
			 }catch(Exception e) {
				 e.printStackTrace();
			 }
		 }
	 }
	 
	 private List<Event> readFromCache(Long repoId) throws Exception {
		 List<Event> value= null;
		 try {
			 final IMap<Long, List<Event>> hazelcastMap = hazelcastInstance.getMap(mapName);
			 InternalCompletableFuture<List<Event>> featureValue = (InternalCompletableFuture<List<Event>>) hazelcastMap.getAsync(repoId);
			 value = featureValue.get(hazelcastReadTimeOut,TimeUnit.MICROSECONDS);
			 if (value != null) {
				 log.debug("Read from cache key {}", repoId);
			 }
		 }catch(TimeoutException e) {
			 log.error("Time out exception"+e);
		 }catch(Exception e) {
			 log.error("Time out exception"+e);
		 }	 
		 return value;
	 }
}
