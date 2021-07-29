package com.ttech.orch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.ttech.orch.model.Event;
import com.ttech.orch.service.impl.EventServiceImpl;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.collection.Stream;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
public class CircuiteBreakerTest {

	@Autowired
	private CircuitBreakerRegistry circuitBreakerRegistry;
	
	@MockBean(name= "kafkaRestTemplate")
	RestTemplate kafkaRestTemplate;
	
	@SpyBean
	EventServiceImpl eventServiceImpl;
	
	
	@BeforeEach
	public void setUpCircuitBreakers() throws Exception{
		ReflectionTestUtils.setField(eventServiceImpl, "kafkaRestTemplate", kafkaRestTemplate);
		circuitBreakerRegistry.getAllCircuitBreakers().toStream().forEach(CircuitBreaker::reset);
		circuitBreakerRegistry.getAllCircuitBreakers().toStream().forEach(b -> {
			System.out.println(b.getName()+" "+ b.getCircuitBreakerConfig().getMinimumNumberOfCalls());
		});
	}
	
	@Test
	public void testCircuitBreakers() throws Exception{
		CircuitBreaker circuiteBreaker = circuitBreakerRegistry.circuitBreaker("events_actorid_repoid");
		System.out.println(circuiteBreaker.getName());
		System.out.println(circuiteBreaker.getCircuitBreakerConfig().getMinimumNumberOfCalls());
		
		ResponseEntity<List<Event>> response = new ResponseEntity<List<Event>>(new ArrayList<Event>(), HttpStatus.OK);
		HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
		
		Mockito.when(kafkaRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<List<Event>>() {
		}))).thenThrow(exception).thenThrow(exception).thenReturn(response);
		
		//force circuit breaker into Open Status
		Stream.range(1, 3).forEach((x) -> {
			try {
				System.out.println("Test"+x);
				eventServiceImpl.getEventsByActorIdRepoId(123L, 123L);
				Thread.sleep(100);
				
			}catch(Exception e) {
				System.out.println("Exception:"+ e.getMessage()+ e.getClass());
				System.out.println("Client Suppressing exception and retrying");
			}
		});
		
		//Verify Circuit breaker is open
		System.out.println(circuiteBreaker.getName() + " "+ circuiteBreaker.getState().toString());
		assertThat(circuiteBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
		
		//Wait 8  seconds while circuit breaker is Open status
		Thread.sleep(8000);
		
		// Verify circuit breaker transitioned into  Half_Open Status
		System.out.println(circuiteBreaker.getName()+circuiteBreaker.getState().toString());
		assertThat(circuiteBreaker.getState()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
		
		//Provide more traffic to push circuit breaker into Closed status
		Stream.range(1, 5).forEach((x) -> {
			try {
				System.out.println("Test"+x);
				eventServiceImpl.getEventsByActorIdRepoId(123L, 123L);
				Thread.sleep(100);
			}catch(Exception e) {
				System.out.println("Client Suppressing exception and retrying");
			}
			
		});
		
		//verify circuit breaker is in closed status
		System.out.println(circuiteBreaker.getName() + circuiteBreaker.getState().toString());
		assertThat(circuiteBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
	}
	
	
	
	public void testIgnoresNotFoundException() {
		CircuitBreaker circuiteBreaker = circuitBreakerRegistry.circuitBreaker("events_actorid_repoid");
		HttpClientErrorException exception = new HttpClientErrorException(HttpStatus.NOT_FOUND);
		
		Mockito.when(kafkaRestTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(new ParameterizedTypeReference<List<Event>>() {
		}))).thenThrow(exception);
		
		//Test CB with ignorable exceptions
		Stream.range(1, 10).forEach((x) -> {
			try {
				System.out.println("Test"+x);
				eventServiceImpl.getEventsByActorIdRepoId(123L, 123L);
				Thread.sleep(100);
			}catch(Exception e) {
				System.out.println("Exception:"+ e.getMessage()+ e.getClass());
				System.out.println("Client Suppressing exception and retrying");
			}
	
		});
		//verify circuit breaker is in closed status
		System.out.println(circuiteBreaker.getName() + circuiteBreaker.getState().toString());
		assertThat(circuiteBreaker.getState()).isEqualTo(CircuitBreaker.State.CLOSED);
		
	}
}
