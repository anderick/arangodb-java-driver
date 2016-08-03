package com.arangodb.model;

import java.util.concurrent.Future;

import com.arangodb.ArangoException;
import com.arangodb.entity.BaseDocument;
import com.arangodb.entity.BooleanResultEntity;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class Test {

	public static void main(final String[] args) throws ArangoException {

		final ArangoDB arangoDB = new ArangoDB();
		{
			final DatabaseCreateOptions options = new DatabaseCreateOptions();
			final BooleanResultEntity result = arangoDB.createDB("testDdb", options);
		}
		{
			final DatabaseCreateOptions options = new DatabaseCreateOptions();
			final Callback<BooleanResultEntity> callback = null;
			final Future<BooleanResultEntity> result = arangoDB.createDB("testDb", options, callback);
		}

		{
			final DatabaseCreateOptions options = new DatabaseCreateOptions();
			final BooleanResultEntity result = arangoDB.createDB("testDb").with(options).execute();
		}
		{
			final DatabaseCreateOptions options = new DatabaseCreateOptions();
			final Callback<BooleanResultEntity> callback = null;
			final Future<BooleanResultEntity> result = arangoDB.createDB("testDb").with(options).execute(callback);
		}
		{
			final CollectionCreateOptions options = new CollectionCreateOptions();
			arangoDB.db("testDb").createCollection("testCol").with(options)
					.execute(r -> System.out.println(r.getChecksum()));
		}
		{
			final DocumentReadOptions options = new DocumentReadOptions();
			arangoDB.db("testDb").collection("coll1").readDocument("key1", BaseDocument.class).with(options).execute();
		}
		{
			final Object myClass = null;
			final DocumentUpdateOptions options = new DocumentUpdateOptions();
			options.setRev("rev1");
			options.setWaitForSync(false);
			options.setKeepNull(false);
			arangoDB.db("testDb").collection("coll1").updateDocument("key1", myClass).with(options).execute();
		}
	}

}
