package com.ttech.orch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCache;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.HazelcastCacheMetrics;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class HazelCastConfiguration {

	private static final String INSTANCE_NAME = "orch-service";
	
	@Value("${hazelcast.map.name: orchMap}")
	private String mapName;
	
	@Value("${hazelcast.ttl.sec:60}")
	private int hazelCastTtl;
	
	@Value("${hazelcast.max.idle:60}")
	private int hazelCastMaxIdle;
	
	@Value("${hazelcast.max.size:500}")
	private int hazelCastMaxSize;
	
	
	@Autowired
	MeterRegistry meterRegistry;
	
	@Autowired
	Environment environment;
	
	@Bean
	public HazelcastInstance hazelcastInstance() {
		HazelcastInstance hazelcastInstance = Hazelcast.getHazelcastInstanceByName(INSTANCE_NAME);
		if(hazelcastInstance == null) {
			log.info("HazelcastInstance {} ttl: {} idle: {}",mapName,  hazelCastTtl, hazelCastMaxIdle, hazelCastMaxSize);
			Config config = new Config();
			if(CloudPlatform.getActive(environment) == CloudPlatform.KUBERNETES) {
				log.info("CloudPlatform.KUBERNETES-HazelcastInstance {} ttl: {} idle: {}",mapName,  hazelCastTtl, hazelCastMaxIdle, hazelCastMaxSize);
				JoinConfig joinConfig = config.getNetworkConfig().getJoin();
				joinConfig.getMulticastConfig().setEnabled(false);
				joinConfig.getKubernetesConfig().setEnabled(true);
			}else {
				log.info("HazelcastInstance local");
				JoinConfig joinConfig = config.getNetworkConfig().getJoin();
				joinConfig.getMulticastConfig().setEnabled(false);
				joinConfig.getKubernetesConfig().setEnabled(false);
			}
			
			config.setInstanceName(INSTANCE_NAME).addMapConfig(
					new MapConfig().setName(mapName)
					.setEvictionConfig(new EvictionConfig().setEvictionPolicy(EvictionPolicy.LRU))
					.setTimeToLiveSeconds(hazelCastTtl)
					.setMaxIdleSeconds(hazelCastMaxIdle)
					);
			config.setProperty("hazelcast.backpressure.enabled", "true");
			hazelcastInstance = Hazelcast.newHazelcastInstance(config);
			HazelcastCacheMetrics hzMetrics = new HazelcastCacheMetrics(hazelcastInstance.getMap(mapName), null);
			hzMetrics.bindTo(meterRegistry);
		}
		return hazelcastInstance;
	}
}
