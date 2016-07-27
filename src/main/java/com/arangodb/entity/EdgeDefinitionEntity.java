package com.arangodb.entity;

import java.util.ArrayList;
import java.util.List;

public class EdgeDefinitionEntity {

	private String collection;
	private List<String> from;
	private List<String> to;

	public EdgeDefinitionEntity() {
		this.from = new ArrayList<String>();
		this.to = new ArrayList<String>();
	}

	public String getCollection() {
		return collection;
	}

	public EdgeDefinitionEntity setCollection(final String collection) {
		this.collection = collection;
		return this;
	}

	public List<String> getFrom() {
		return from;
	}

	public EdgeDefinitionEntity setFrom(final List<String> from) {
		this.from = from;
		return this;
	}

	public List<String> getTo() {
		return to;
	}

	public EdgeDefinitionEntity setTo(final List<String> to) {
		this.to = to;
		return this;
	}
}
