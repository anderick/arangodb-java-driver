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

import java.io.Serializable;
import java.util.List;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ReplicationInventoryEntity extends BaseEntity {

	private List<Collection> collections;
	private ReplicationState state;
	private long tick;

	public List<Collection> getCollections() {
		return collections;
	}

	public ReplicationState getState() {
		return state;
	}

	public long getTick() {
		return tick;
	}

	public void setCollections(final List<Collection> collections) {
		this.collections = collections;
	}

	public void setState(final ReplicationState state) {
		this.state = state;
	}

	public void setTick(final long tick) {
		this.tick = tick;
	}

	public static class CollectionParameter implements Serializable {
		private int version;
		private CollectionType type;
		private long cid;
		private boolean deleted;
		private boolean doCompact;
		private long maximalSize;
		private String name;
		private boolean isVolatile;
		private boolean waitForSync;

		public int getVersion() {
			return version;
		}

		public CollectionType getType() {
			return type;
		}

		public long getCid() {
			return cid;
		}

		public boolean isDeleted() {
			return deleted;
		}

		public boolean isDoCompact() {
			return doCompact;
		}

		public long getMaximalSize() {
			return maximalSize;
		}

		public String getName() {
			return name;
		}

		public boolean isVolatile() {
			return isVolatile;
		}

		public boolean isWaitForSync() {
			return waitForSync;
		}

		public void setVersion(final int version) {
			this.version = version;
		}

		public void setType(final CollectionType type) {
			this.type = type;
		}

		public void setCid(final long cid) {
			this.cid = cid;
		}

		public void setDeleted(final boolean deleted) {
			this.deleted = deleted;
		}

		public void setDoCompact(final boolean doCompact) {
			this.doCompact = doCompact;
		}

		public void setMaximalSize(final long maximalSize) {
			this.maximalSize = maximalSize;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setVolatile(final boolean isVolatile) {
			this.isVolatile = isVolatile;
		}

		public void setWaitForSync(final boolean waitForSync) {
			this.waitForSync = waitForSync;
		}
	}

	public static class Collection implements Serializable {
		private CollectionParameter parameter;
		private List<IndexEntity> indexes;

		public CollectionParameter getParameter() {
			return parameter;
		}

		public List<IndexEntity> getIndexes() {
			return indexes;
		}

		public void setParameter(final CollectionParameter parameter) {
			this.parameter = parameter;
		}

		public void setIndexes(final List<IndexEntity> indexes) {
			this.indexes = indexes;
		}

	}

}
