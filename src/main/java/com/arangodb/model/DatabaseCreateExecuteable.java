package com.arangodb.model;

import java.util.concurrent.Future;

import com.arangodb.ArangoException;
import com.arangodb.entity.BooleanResultEntity;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class DatabaseCreateExecuteable implements Executeable<BooleanResultEntity> {

	protected DatabaseCreateExecuteable() {
		super();
	}

	public DatabaseCreateExecuteable with(final DatabaseCreateOptions options) {
		return this;
	}

	@Override
	public Future<BooleanResultEntity> execute(final Callback<BooleanResultEntity> callback) throws ArangoException {
		return null;
	}

}
