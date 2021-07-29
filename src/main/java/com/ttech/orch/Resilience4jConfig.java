package com.ttech.orch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.collection.Seq;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class Resilience4jConfig {
	
	@Autowired
	CircuitBreakerRegistry circuitBreakerRegistry;
	
	@Autowired
	RetryRegistry retryRegistry;

	@Bean
	public Seq<CircuitBreaker> circuitBreaker(){
		log.info("circuitBreaker");
		//get or Create CircuitBreaker from the CircuitBreakerRegistry with custom configuration.
		
		Seq<CircuitBreaker> circuitBreaker = circuitBreakerRegistry.getAllCircuitBreakers();
		log.info("{} Entering circuitBreaker() size: {}, names:{}", circuitBreaker.size(), circuitBreakerRegistry.getAllCircuitBreakers());
		//Apply event listener for existing circuit breaker instances
		circuitBreaker.forEach(cb -> cb.getEventPublisher().onStateTransition(event -> log.info((event.toString()))));
		return circuitBreaker;
	}
	
	@Bean
	public Seq<Retry> retry(){
		log.info("retry");
		Seq<Retry> retries = retryRegistry.getAllRetries();
		log.info("Entering Retries() size:{}, name:{}",retries.size(), retryRegistry.getAllRetries() );
		//Apply event listener for existing retry instances
		retries.forEach(ret -> ret.getEventPublisher().onRetry(event -> log.info(event.toString())));
		return retries;
	}
}
