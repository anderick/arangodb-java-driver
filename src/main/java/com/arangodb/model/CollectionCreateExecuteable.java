package com.arangodb.model;

import java.util.concurrent.Future;

import com.arangodb.ArangoException;
import com.arangodb.entity.CollectionEntity;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class CollectionCreateExecuteable implements Executeable<CollectionEntity> {

	protected CollectionCreateExecuteable(final DatabaseModel databaseModel, final String name) {
	}

	public CollectionCreateExecuteable with(final CollectionCreateOptions options) {
		return this;
	}

	@Override
	public Future<CollectionEntity> execute(final Callback<CollectionEntity> callback) throws ArangoException {
		return null;
	}

}
