package com.ttech.orch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.httpcomponents.PoolingHttpClientConnectionManagerMetricsBinder;

@Configuration
public class RestTemplateConfig {

	@Value("${http.conn.time.to.live}")
	private long httpConnTimeToLive;

	@Value("${http.evict.idle.conn}")
	private long httpEvictIdleConn;

	@Value("${http.conn.time.out}")
	private int httpConntimeOut;

	@Value("${http.read.time.out}")
	private int httpReadTimeOut;

	@Value("${http.conn.request.timeout}")
	private int httpConnRequestTimeOut;

	@Value("${http.client.max.conn.route}")
	private int httpClientMaxConnRoute;

	@Value("${http.client.max.conn.total}")
	private int httpClientMaxConnTotal;

	@Autowired
	MeterRegistry registry;

	@Bean
	public RestTemplate kafkaRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		// create pooled connection manager and set pool max values and bind the pool
		// metrics to the meter and registry

		PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(httpConnTimeToLive,
				TimeUnit.MINUTES);
		connMgr.setDefaultMaxPerRoute(httpClientMaxConnRoute);
		connMgr.setMaxTotal(httpClientMaxConnTotal);
		PoolingHttpClientConnectionManagerMetricsBinder binder = new PoolingHttpClientConnectionManagerMetricsBinder(
				connMgr, "resttemplate-http-client");
		binder.bindTo(registry);

		// create http client using the connection Manager
		HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connMgr).disableCookieManagement()
				.evictExpiredConnections().evictIdleConnections(httpEvictIdleConn, TimeUnit.SECONDS)
				.setKeepAliveStrategy(connectionKeepAliveStrategy()).build();

		// create request factory with client and set timeout values
		HttpComponentsClientHttpRequestFactory reqFactory = new HttpComponentsClientHttpRequestFactory();
		reqFactory.setConnectionRequestTimeout(httpConnRequestTimeOut);
		reqFactory.setReadTimeout(httpReadTimeOut);
		reqFactory.setConnectionRequestTimeout(httpConnRequestTimeOut);
		reqFactory.setHttpClient(httpClient);

		// build rest Template with the request factory and add interceptors
		List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();

		RestTemplate restTemplate = restTemplateBuilder.build();
		restTemplate.setRequestFactory(reqFactory);
		restTemplate.setInterceptors(interceptors);
		return restTemplate;

	}

	private ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return (httpResponse, httpContext) -> {
			HeaderIterator headerIterator = httpResponse.headerIterator(HTTP.CONN_KEEP_ALIVE);
			HeaderElementIterator elementIterator = new BasicHeaderElementIterator(headerIterator);
			while (elementIterator.hasNext()) {
				HeaderElement element = elementIterator.nextElement();
				String param = element.getName();
				String value = element.getValue();
				if (value != null && param.equalsIgnoreCase("timeout")) {
					return Long.parseLong(value) * 1000; // convert to ms
				}
			}
			return httpConnTimeToLive * 60000;
		};
	}
}
