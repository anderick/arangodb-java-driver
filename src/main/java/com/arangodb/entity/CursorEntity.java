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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.arangodb.NonUniqueResultException;
import com.arangodb.util.CollectionUtils;

/**
 * Cursor entity that represents the result of a AQL query.
 *
 * @author tamtam180 - kirscheless at gmail.com
 * @author mrbatista
 *
 */
public class CursorEntity<T> extends BaseEntity implements Iterable<T> {

	/**
	 * True if the cursor has more results.
	 */
	private boolean hasMore;

	/**
	 * The amount of results in the cursor
	 */
	private int count = -1;

	/**
	 * The number of results before the final LIMIT
	 */
	private int fullCount = -1;

	/**
	 * The cursor id
	 */
	private long cursorId = -1;

	/**
	 * a boolean flag indicating whether the query result was served from the
	 * query cache or not. If the query result is served from the query cache,
	 * the extra return attribute will not contain any stats sub-attribute and
	 * no profile sub-attribute. (since ArangoDB 2.7)
	 */
	private boolean cached = false;

	/**
	 * A list of bind variables returned by the query
	 */
	private List<String> bindVars;

	/**
	 * A list of extra data returned by the query
	 */
	private Map<String, Object> extra;

	/**
	 * A list of objects containing the results
	 */
	private List<T> results;

	/**
	 * A list of warnings
	 */
	private List<WarningEntity> warnings;

	@Override
	public Iterator<T> iterator() {
		return CollectionUtils.safetyIterator(results);
	}

	/**
	 * The size of the cursor results.
	 *
	 * @return int
	 */
	public int size() {
		if (results == null) {
			return 0;
		}
		return results.size();
	}

	/**
	 * Returns the cursor element at position *index*
	 *
	 * @param index
	 * @return Object
	 */
	public T get(final int index) {
		rangeCheck(index);
		return results.get(index);
	}

	private void rangeCheck(final int index) {
		final int size = size();
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}

	/**
	 * Returns the DocumentEntity objects of this CursorDocumentEntity
	 * 
	 * @return list of DocumentEntity objects
	 */
	public List<T> getResults() {
		return results;
	}

	/**
	 * Return a single instance that matches the query, or null if the query
	 * returns no results.
	 * 
	 * Throws NonUniqueResultException (RuntimeException) if there is more than
	 * one matching result
	 * 
	 * @return the single result or null
	 */
	public T getUniqueResult() {
		final int size = size();
		if (size == 0) {
			return null;
		}
		if (size > 1) {
			throw new NonUniqueResultException(size);
		}
		return get(0);
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public boolean hasMore() {
		return hasMore;
	}

	public int getFullCount() {
		return fullCount;
	}

	public int getCount() {
		return count;
	}

	public long getCursorId() {
		return cursorId;
	}

	public List<String> getBindVars() {
		return bindVars;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public void setResults(final List<T> results) {
		this.results = results;
	}

	public void setHasMore(final boolean hasMore) {
		this.hasMore = hasMore;
	}

	public void setFullCount(final int count) {
		this.fullCount = count;
	}

	public void setCount(final int count) {
		this.count = count;
	}

	public void setCursorId(final long cursorId) {
		this.cursorId = cursorId;
	}

	public void setBindVars(final List<String> bindVars) {
		this.bindVars = bindVars;
	}

	public void setExtra(final Map<String, Object> extra) {
		this.extra = extra;
	}

	public boolean isCached() {
		return cached;
	}

	public void setCached(final boolean cached) {
		this.cached = cached;
	}

	public List<WarningEntity> getWarnings() {
		return warnings;
	}

	public void setWarnings(final List<WarningEntity> warnings) {
		this.warnings = warnings;
	}

	public boolean hasWarnings() {
		return CollectionUtils.isNotEmpty(this.warnings);
	}
}
