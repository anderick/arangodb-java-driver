package com.arangodb.model;

import java.util.Optional;

import com.arangodb.ArangoException;
import com.arangodb.entity.DocumentEntity;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class Document {

	private final Collection collection;
	private final String key;

	private Optional<Boolean> waitForSync = Optional.empty();
	private Optional<String> rev = Optional.empty();
	private final Optional<Boolean> keepNull = Optional.empty();

	protected Document(final Collection collection, final String key) {
		super();
		this.collection = collection;
		this.key = key;
	}

	public <T> DocumentEntity<T> create(final T value) throws ArangoException {
		return null;
	}

	public <T> DocumentEntity<T> replace(final T value) throws ArangoException {
		return null;
	}

	public <T> DocumentEntity<T> update(final T value) throws ArangoException {
		return null;
	}

	public Document rev(final String rev) {
		this.rev = Optional.of(rev);
		return this;
	}

	public Document waitForSync(final Boolean waitForSync) {
		this.waitForSync = Optional.of(waitForSync);
		return this;
	}
}
