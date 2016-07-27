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

import java.io.Serializable;

import com.arangodb.annotations.Exclude;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public abstract class BaseEntity implements Serializable {

	/**
	 * If true an error occurred while creating this entity
	 */
	@Exclude(deserialize = false)
	private boolean error;

	/**
	 * The http response code of the response
	 */
	@Exclude(deserialize = false)
	private int code;

	/**
	 * The Arango error number of the error
	 */
	@Exclude(deserialize = false)
	private int errorNumber;

	/**
	 * If an error occurred this is the error message
	 */
	@Exclude(deserialize = false)
	private String errorMessage;

	/**
	 * The http status code of the response
	 */
	@Exclude(deserialize = false)
	private int statusCode;

	/**
	 * The check sum of the requested resource
	 */
	@Exclude(deserialize = false)
	private String etag;

	/**
	 * The requestId, this attribute is only used for batch requests.
	 */
	@Exclude(deserialize = false)
	private String requestId;

	/**
	 * If the resource has been modified it returns true
	 *
	 * @return boolean
	 */
	public boolean isNotModified() {
		return statusCode == 304;
	}

	/**
	 * If the request is unauthorized this returns true
	 *
	 * @return boolean
	 */
	public boolean isUnauthorized() {
		return statusCode == 401;
	}

	/**
	 * If this is the response of a batch request it returns true
	 *
	 * @return boolean
	 */
	public boolean isBatchResponseEntity() {
		return statusCode == 206;
	}

	public boolean isError() {
		return error;
	}

	public void setError(final boolean error) {
		this.error = error;
	}

	public int getCode() {
		return code;
	}

	public int getErrorNumber() {
		return errorNumber;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setCode(final int code) {
		this.code = code;
	}

	public void setErrorNumber(final int errorNumber) {
		this.errorNumber = errorNumber;
	}

	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(final String etag) {
		this.etag = etag;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(final int statusCode) {
		this.statusCode = statusCode;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(final String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "BaseEntity [error=" + error + ", code=" + code + ", errorNumber=" + errorNumber + ", errorMessage="
				+ errorMessage + ", statusCode=" + statusCode + ", etag=" + etag + ", requestId=" + requestId + "]";
	}

}
