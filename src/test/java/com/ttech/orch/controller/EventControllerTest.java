package com.ttech.orch.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ttech.orch.service.EventService;

@ExtendWith(SpringExtension.class)
public class EventControllerTest {

	private MockMvc mockMvc;
	
	@Mock
	private EventService eventService;
 
	@InjectMocks
	private EventController eventController;
	
	@Test
	public void test_getEventsByRepoId() throws Exception {
		//RequestBuilder builder = MockMvcRequestBuilders.get("/authenticate").content(toJson(reequest)).contentType(MediaType.APPLICATION_JSON_VALUE);
		//mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
		
	}
}
