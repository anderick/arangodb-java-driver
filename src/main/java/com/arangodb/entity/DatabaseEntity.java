/*
 * Copyright (C) 2012,2013 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arangodb.entity;

/**
 * An entity representing an ArangoDB database
 *
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class DatabaseEntity extends BaseEntity {

	/**
	 * The name of the database
	 */
	private String name;

	/**
	 * The id of the database
	 */
	private String id;

	/**
	 * The path of the database
	 */
	private String path;

	/**
	 * If true the database is the system database
	 */
	private boolean isSystem;

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getPath() {
		return path;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public void setSystem(final boolean isSystem) {
		this.isSystem = isSystem;
	}

}
