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
 * The configuration of the replication applier. The configuration can only be
 * changed while the applier is not running. The updated configuration will be
 * saved immediately but only become active with the next start of the applier.
 *
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ReplicationApplierConfigEntity extends BaseEntity {

	/**
	 * the logger server to connect to (e.g. "tcp://192.168.173.13:8529"). The
	 * endpoint must be specified.
	 */
	private String endpoint;

	/**
	 * the name of the database on the endpoint. If not specified, defaults to
	 * the current local database name.
	 */
	private String database;

	/**
	 * an optional ArangoDB username to use when connecting to the endpoint.
	 */
	private String username;

	/**
	 * the password to use when connecting to the endpoint.
	 */
	private String password;

	/**
	 * the maximum number of connection attempts the applier will make in a row.
	 * If the applier cannot establish a connection to the endpoint in this
	 * number of attempts, it will stop itself.
	 */
	private Integer maxConnectRetries;

	/**
	 * the timeout (in seconds) when attempting to connect to the endpoint. This
	 * value is used for each connection attempt.
	 */
	private Integer connectTimeout;

	/**
	 * the timeout (in seconds) for individual requests to the endpoint.
	 */
	private Integer requestTimeout;

	/**
	 * the requested maximum size for log transfer packets that is used when the
	 * endpoint is contacted.
	 */
	private Integer chunkSize;

	/**
	 * whether or not to auto-start the replication applier on (next and
	 * following) server starts
	 */
	private Boolean autoStart;

	/**
	 * if set to true, the replication applier will fall to sleep for an
	 * increasingly long period in case the logger server at the endpoint does
	 * not have any more replication events to apply.
	 */
	private Boolean adaptivePolling;

	public String getEndpoint() {
		return endpoint;
	}

	public String getDatabase() {
		return database;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public Integer getMaxConnectRetries() {
		return maxConnectRetries;
	}

	public Integer getConnectTimeout() {
		return connectTimeout;
	}

	public Integer getRequestTimeout() {
		return requestTimeout;
	}

	public Integer getChunkSize() {
		return chunkSize;
	}

	public Boolean getAutoStart() {
		return autoStart;
	}

	public Boolean getAdaptivePolling() {
		return adaptivePolling;
	}

	public void setEndpoint(final String endpoint) {
		this.endpoint = endpoint;
	}

	public void setDatabase(final String database) {
		this.database = database;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setMaxConnectRetries(final Integer maxConnectRetries) {
		this.maxConnectRetries = maxConnectRetries;
	}

	public void setConnectTimeout(final Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setRequestTimeout(final Integer requestTimeout) {
		this.requestTimeout = requestTimeout;
	}

	public void setChunkSize(final Integer chunkSize) {
		this.chunkSize = chunkSize;
	}

	public void setAutoStart(final Boolean autoStart) {
		this.autoStart = autoStart;
	}

	public void setAdaptivePolling(final Boolean adaptivePolling) {
		this.adaptivePolling = adaptivePolling;
	}

}
