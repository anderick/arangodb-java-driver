package com.arangodb.model;

import com.arangodb.ArangoException;
import com.arangodb.entity.BooleanResultEntity;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class Database {

	private final String name;

	public Database(final String name) {
		super();
		this.name = name;
	}

	public Collection collection(final String name) {
		return new Collection(this, name);
	}

	public BooleanResultEntity create() throws ArangoException {
		return null;
	}

	public BooleanResultEntity delete() throws ArangoException {
		return null;
	}

}
