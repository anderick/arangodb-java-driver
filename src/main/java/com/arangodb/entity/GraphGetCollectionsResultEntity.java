package com.arangodb.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gschwab
 *
 */
public class GraphGetCollectionsResultEntity extends BaseEntity {

	private List<String> collections;

	public List<String> getCollections() {
		if (collections == null) {
			collections = new ArrayList<String>();
		}
		return collections;
	}

	public void setCollections(final List<String> collections) {
		this.collections = collections;
	}

}