package com.ttech.orch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Event {
	
    private Long id;
    private String type;
    private Actor actor;
    private Repo repo;
    private String created_at;
}
