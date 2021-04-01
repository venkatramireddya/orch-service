package com.ttech.orch;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfig {
	
	@Value("${gen.await.term.secs}")
	private int genAwaitTermSecs;
	
	@Value("${gen.core.pool.size}")
	private int genCorePoolSize;
	
	@Value("${gen.keep.alive.seconds}")
	private int genKeepAliveSeconds;
	
	@Value("${gen.max.pool.size}")
	private int genMaxPoolSize;
	
	@Value("${gen.queue.capacity}")
	private int genQueueCapacity;
	
	@Bean
	@Qualifier("executorConfigaration")
	public Executor executorConfigaration() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setAwaitTerminationSeconds(genAwaitTermSecs);
		executor.setCorePoolSize(genCorePoolSize);
		executor.setKeepAliveSeconds(genKeepAliveSeconds);
		executor.setMaxPoolSize(genMaxPoolSize);
		executor.setQueueCapacity(genQueueCapacity);
		executor.setThreadNamePrefix("executorConfig-");
		executor.initialize();
		return executor;
	}

}
