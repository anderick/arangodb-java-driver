/*
 * Copyright (C) 2015 ArangoDB GmbH
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

package com.arangodb.example.graph;

import com.arangodb.entity.BaseDocument;
import com.arangodb.velocypack.annotations.SerializedName;

public class Knows {

	@SerializedName(BaseDocument.ID)
	private String documentHandle;

	@SerializedName(BaseDocument.KEY)
	private String documentKey;

	@SerializedName(BaseDocument.REV)
	private long documentRevision;

	@SerializedName(BaseDocument.FROM)
	String fromVertexHandle;

	@SerializedName(BaseDocument.TO)
	String toVertexHandle;

	private Integer since;

	public Knows() {
	}

	public Knows(final Integer since) {
		this.since = since;
	}

	public Integer getSince() {
		return since;
	}

	public void setSince(final Integer since) {
		this.since = since;
	}

	public String getDocumentHandle() {
		return documentHandle;
	}

	public void setDocumentHandle(final String documentHandle) {
		this.documentHandle = documentHandle;
	}

	public String getDocumentKey() {
		return documentKey;
	}

	public void setDocumentKey(final String documentKey) {
		this.documentKey = documentKey;
	}

	public long getDocumentRevision() {
		return documentRevision;
	}

	public void setDocumentRevision(final long documentRevision) {
		this.documentRevision = documentRevision;
	}

	public String getFromVertexHandle() {
		return fromVertexHandle;
	}

	public String getToVertexHandle() {
		return toVertexHandle;
	}

	public void setFromVertexHandle(final String fromVertexHandle) {
		this.fromVertexHandle = fromVertexHandle;
	}

	public void setToVertexHandle(final String toVertexHandle) {
		this.toVertexHandle = toVertexHandle;
	}

}
