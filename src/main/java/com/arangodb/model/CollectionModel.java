package com.arangodb.model;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class CollectionModel {

	private final DatabaseModel databaseModel;
	private final String name;

	protected CollectionModel(final DatabaseModel databaseModel, final String name) {
		this.databaseModel = databaseModel;
		this.name = name;
	}

	public DocumentReadExecuteable readDocument(final String key, final Class<?> type) {
		return new DocumentReadExecuteable(this, key);
	}

	public <T> DocumentUpdateExecuteable<T> updateDocument(final String key, final T value) {
		return new DocumentUpdateExecuteable<>(this, key, value);
	}

}
