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

package com.arangodb.http;

import java.io.InputStream;
import java.util.Map;

import com.arangodb.entity.EntityFactory;
import com.arangodb.velocypack.VPackSlice;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class HttpResponseEntity {

	private int statusCode;
	private String statusPhrase;
	// private String text;
	private VPackSlice content;
	private InputStream stream;
	private String etag;
	private String requestId;
	private Map<String, String> headers;
	private String contentType;

	/**
	 * Cheks if the content type is "application/x-velocypack"
	 * 
	 * @return true if the content type is "application/x-velocypack"
	 * @since 3.1.0
	 */
	public boolean isVPackResponse() {
		return contentType != null && contentType.startsWith("application/x-velocypack");
	}

	/**
	 * Checks if the content type is "application/json"
	 * 
	 * @return true if the content type is "application/json"
	 * @since 1.4.0
	 */
	public boolean isJsonResponse() {
		return contentType != null && contentType.startsWith("application/json");
	}

	/**
	 * Checks if the content type is "application/x-arango-dump"
	 * 
	 * @return true if the content type is "application/x-arango-dump"
	 * @since 1.4.0
	 */
	public boolean isDumpResponse() {
		return contentType != null && contentType.startsWith("application/x-arango-dump");
	}

	/**
	 * Checks if the content type is "text/plain"
	 * 
	 * @return true if the content type is "text/plain"
	 * @since 1.4.0
	 */
	public boolean isTextResponse() {
		return contentType != null && contentType.startsWith("text/plain");
	}

	public boolean isBatchRepsonse() {
		return requestId != null;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public InputStream getStream() {
		return stream;
	}

	public void setStream(final InputStream stream) {
		this.stream = stream;
	}

	public String getStatusPhrase() {
		return statusPhrase;
	}

	// public String getText() {
	// return text;
	// }

	public void setStatusCode(final int statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusPhrase(final String statusPhrase) {
		this.statusPhrase = statusPhrase;
	}

	// public void setText(final String text) {
	// this.text = text;
	// }

	public String getEtag() {
		return etag;
	}

	public VPackSlice getContent() {
		return content;
	}

	public void setContent(final VPackSlice content) {
		this.content = content;
	}

	public void setEtag(final String etag) {
		this.etag = etag;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(final Map<String, String> headers) {
		this.headers = headers;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(final String contentType) {
		this.contentType = contentType;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(final String requestId) {
		this.requestId = requestId;
	}

	public String createStatusPhrase() {
		String result;
		switch (statusCode) {
		case 400:
			result = "Bad Request";
			break;
		case 401:
			result = "Unauthorized";
			break;
		case 403:
			result = "Forbidden";
			break;
		case 404:
			result = "Not Found";
			break;
		case 405:
			result = "Method Not Allowed";
			break;
		case 406:
			result = "Not Acceptable";
			break;
		case 407:
			result = "Proxy Authentication Required";
			break;
		case 408:
			result = "Request Time-out";
			break;
		case 409:
			result = "Conflict";
			break;
		case 500:
			result = "Internal Server Error: " + EntityFactory.toJson(content);
			break;
		default:
			result = "unknown error";
			break;
		}
		return result;
	}
}
