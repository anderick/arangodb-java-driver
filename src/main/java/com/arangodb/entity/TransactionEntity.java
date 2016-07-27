/*
 * Copyright (C) 2012 tamtam180
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class TransactionEntity extends BaseEntity {

	public static class ReadWriteCollections {

		private final List<String> read = new ArrayList<String>();

		private final List<String> write = new ArrayList<String>();

		private boolean allowImplicit = true;

		public boolean isAllowImplicit() {
			return allowImplicit;
		}

		public void setAllowImplicit(final boolean allowImplicit) {
			this.allowImplicit = allowImplicit;
		}
	}

	private ReadWriteCollections collections = new ReadWriteCollections();

	private String action;

	private Boolean waitForSync;

	private int lockTimeout;

	private Object params;

	public TransactionEntity(final String action) {
		this.action = action;
	}

	public ReadWriteCollections getCollections() {
		return collections;
	}

	public void setCollections(final ReadWriteCollections collections) {
		this.collections = collections;
	}

	public void addReadCollection(final String collection) {
		this.collections.read.add(collection);
	}

	public void addWriteCollection(final String collection) {
		this.collections.write.add(collection);
	}

	/**
	 * @param allowImplicit
	 *            allows(true)/ disallows(false) read access to other
	 *            collections than specified. Default is true.
	 */
	public void setAllowImplicit(final boolean allowImplicit) {
		collections.setAllowImplicit(allowImplicit);
	}

	public String getAction() {
		return action;
	}

	public void setAction(final String action) {
		this.action = action;
	}

	public Boolean getWaitForSync() {
		return waitForSync;
	}

	public void setWaitForSync(final Boolean waitForSync) {
		this.waitForSync = waitForSync;
	}

	public int getLockTimeout() {
		return lockTimeout;
	}

	public void setLockTimeout(final int lockTimeout) {
		this.lockTimeout = lockTimeout;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(final Object params) {
		this.params = params;
	}
}
