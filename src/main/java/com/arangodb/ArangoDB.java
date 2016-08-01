package com.arangodb;

import com.arangodb.model.Database;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class ArangoDB {

	private static final String SYSTEM_DB = "_system";

	public Database db() {
		return new Database(SYSTEM_DB);
	}

	public Database db(final String name) {
		return new Database(name);
	}

	public static void main(final String[] args) {
		new ArangoDB().db().collection("").document("").async().create(callback);
	}

}
