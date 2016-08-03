package com.arangodb.model;

import java.util.concurrent.Future;

import com.arangodb.ArangoException;
import com.arangodb.entity.BooleanResultEntity;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class ArangoDB {

	public DatabaseModel db(final String name) {
		return new DatabaseModel(this, name);
	}

	public BooleanResultEntity createDB(final String name, final DatabaseCreateOptions options) throws ArangoException {
		return null;
	}

	public Future<BooleanResultEntity> createDB(
		final String name,
		final DatabaseCreateOptions options,
		final Callback<BooleanResultEntity> callback) throws ArangoException {
		return null;
	}

	public DatabaseCreateExecuteable createDB(final String name) {
		return new DatabaseCreateExecuteable();
	}
}
