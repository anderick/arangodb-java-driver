package com.arangodb.model;

import java.util.concurrent.Future;

import com.arangodb.ArangoException;
import com.arangodb.entity.DocumentResultEntity;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class DocumentReadExecuteable implements Executeable<DocumentResultEntity<?>> {

	protected DocumentReadExecuteable(final CollectionModel collectionModel, final String key) {
	}

	public DocumentReadExecuteable with(final DocumentReadOptions options) {
		return this;
	}

	@Override
	public Future<DocumentResultEntity<?>> execute(final Callback<DocumentResultEntity<?>> callback)
			throws ArangoException {
		return null;
	}

}
