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

import java.util.Map;

import org.apache.http.HttpEntity;

import com.arangodb.velocypack.VPackSlice;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * 
 */
public class HttpRequestEntity {

	public enum RequestType {
		GET, POST, PUT, DELETE, HEAD, PATCH
	}

	private Map<String, Object> headers;
	private RequestType type;
	private String url;
	private Map<String, Object> parameters;
	private String username;
	private String password;
	private VPackSlice body;
	private HttpEntity entity;

	public HttpRequestEntity() {
		super();
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(final Map<String, Object> headers) {
		this.headers = headers;
	}

	public boolean hasHeaders() {
		return headers != null && !headers.isEmpty();
	}

	public RequestType getType() {
		return type;
	}

	public void setType(final RequestType type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(final Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public VPackSlice getBody() {
		return body;
	}

	public void setBody(final VPackSlice body) {
		this.body = body;
	}

	public HttpEntity getEntity() {
		return entity;
	}

	public void setEntity(final HttpEntity entity) {
		this.entity = entity;
	}

}
