package com.arangodb.entity;

import com.arangodb.velocypack.annotations.SerializedName;

public class PlainEdgeEntity extends BaseEntity {

	@SerializedName("_rev")
	private long documentRevision;
	@SerializedName("_id")
	private String documentHandle;
	@SerializedName("_key")
	private String documentKey;
	@SerializedName("_from")
	private String fromCollection;
	@SerializedName("_to")
	private String toCollection;

	public long getDocumentRevision() {
		return documentRevision;
	}

	public void setDocumentRevision(final long documentRevision) {
		this.documentRevision = documentRevision;
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

	public String getFromCollection() {
		return fromCollection;
	}

	public void setFromCollection(final String fromCollection) {
		this.fromCollection = fromCollection;
	}

	public String getToCollection() {
		return toCollection;
	}

	public void setToCollection(final String toCollection) {
		this.toCollection = toCollection;
	}

}
