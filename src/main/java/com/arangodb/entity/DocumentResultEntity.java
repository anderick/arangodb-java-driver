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

import java.util.List;

/**
 * An entity representing a list of documents
 *
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class DocumentResultEntity<T> extends BaseEntity {

	/**
	 * The list of generic document entities
	 */
	private List<DocumentEntity<T>> result;

	/**
	 * Returns the first document from the list
	 *
	 * @return DocumentEntity<T>
	 */
	public DocumentEntity<T> getOne() {
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	/**
	 * The size of the documents list
	 *
	 * @return int
	 */
	public int size() {
		if (result == null) {
			return 0;
		}
		return result.size();
	}

	public List<DocumentEntity<T>> getResult() {
		return result;
	}

	public void setResult(final List<DocumentEntity<T>> result) {
		this.result = result;
	}

}
