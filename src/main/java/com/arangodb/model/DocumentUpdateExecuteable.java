package com.arangodb.model;

import java.util.concurrent.Future;

import com.arangodb.ArangoException;
import com.arangodb.entity.DocumentResultEntity;

/**
 * @author Mark - mark at arangodb.com
 * @param <T>
 *
 */
public class DocumentUpdateExecuteable<T> implements Executeable<DocumentResultEntity<T>> {

	protected DocumentUpdateExecuteable(final CollectionModel collectionModel, final String key, final T value) {
	}

	public DocumentUpdateExecuteable<T> with(final DocumentUpdateOptions options) {
		return this;
	}

	@Override
	public Future<DocumentResultEntity<T>> execute(final Callback<DocumentResultEntity<T>> callback)
			throws ArangoException {
		return null;
	}

}
