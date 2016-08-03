package com.arangodb.model;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class DatabaseModel {

	private final ArangoDB arangoDB;
	private final String name;

	protected DatabaseModel(final ArangoDB arangoDB, final String name) {
		this.arangoDB = arangoDB;
		this.name = name;
	}

	public CollectionModel collection(final String name) {
		return new CollectionModel(this, name);
	}

	public CollectionCreateExecuteable createCollection(final String name) {
		return new CollectionCreateExecuteable(this, name);
	}

}
