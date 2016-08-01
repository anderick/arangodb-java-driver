package com.arangodb.model;

import java.util.List;
import java.util.Optional;

import com.arangodb.ArangoException;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.CollectionType;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class Collection {

	private final Database db;
	private final String name;

	private final Optional<Boolean> waitForSync = Optional.empty();
	private final Optional<Boolean> doCompact = Optional.empty();
	private final Optional<Integer> journalSize = Optional.empty();
	private final Optional<Boolean> isSystem = Optional.empty();
	private final Optional<Boolean> isVolatile = Optional.empty();
	private final Optional<CollectionType> type = Optional.empty();
	// private CollectionKeyOption keyOptions;
	private final Optional<Integer> numberOfShards = Optional.empty();
	private final Optional<List<String>> shardKeys = Optional.empty();

	protected Collection(final Database db, final String name) {
		super();
		this.db = db;
		this.name = name;
	}

	public Document document(final String key) {
		return new Document(this, key);
	}

	public CollectionEntity create() throws ArangoException {
		return null;
	}

	public CollectionEntity get() throws ArangoException {
		return null;
	}

	public CollectionEntity getProperties() throws ArangoException {
		return null;
	}

	public CollectionEntity getRevision() throws ArangoException {
		return null;
	}

	public CollectionEntity getCount() throws ArangoException {
		return null;
	}

	public CollectionEntity getFigures() throws ArangoException {
		return null;
	}

	public CollectionEntity getChecksum() throws ArangoException {
		return null;
	}

	public CollectionEntity load() throws ArangoException {
		return null;
	}

	public CollectionEntity unload() throws ArangoException {
		return null;
	}

	public CollectionEntity truncate() throws ArangoException {
		return null;
	}

	public CollectionEntity rename(final String newName) throws ArangoException {
		return null;
	}

	public CollectionEntity delete() throws ArangoException {
		return null;
	}

}
