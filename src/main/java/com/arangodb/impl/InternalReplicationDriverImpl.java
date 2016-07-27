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

package com.arangodb.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoException;
import com.arangodb.entity.EntityFactory;
import com.arangodb.entity.MapAsEntity;
import com.arangodb.entity.ReplicationApplierConfigEntity;
import com.arangodb.entity.ReplicationApplierStateEntity;
import com.arangodb.entity.ReplicationDumpHeader;
import com.arangodb.entity.ReplicationDumpRecord;
import com.arangodb.entity.ReplicationInventoryEntity;
import com.arangodb.entity.ReplicationLoggerConfigEntity;
import com.arangodb.entity.ReplicationLoggerStateEntity;
import com.arangodb.entity.ReplicationSyncEntity;
import com.arangodb.entity.RestrictType;
import com.arangodb.entity.StreamEntity;
import com.arangodb.http.HttpManager;
import com.arangodb.http.HttpResponseEntity;
import com.arangodb.util.DumpHandler;
import com.arangodb.util.IOUtils;
import com.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class InternalReplicationDriverImpl extends BaseArangoDriverImpl
		implements com.arangodb.InternalReplicationDriver {

	InternalReplicationDriverImpl(final ArangoConfigure configure, final HttpManager httpManager) {
		super(configure, httpManager);
	}

	@Override
	public ReplicationInventoryEntity getReplicationInventory(final String database, final Boolean includeSystem)
			throws ArangoException {

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(database, "/_api/replication/inventory"),
			new MapBuilder().put("includeSystem", includeSystem).get());

		return createEntity(res, ReplicationInventoryEntity.class);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void getReplicationDump(
		final String database,
		final String collectionName,
		final Long from,
		final Long to,
		final Integer chunkSize,
		final Boolean ticks,
		final Class<T> clazz,
		final DumpHandler<T> handler) throws ArangoException {

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(database, "/_api/replication/dump"),
			new MapBuilder().put(COLLECTION, collectionName).put("from", from).put("to", to).put("chunkSize", chunkSize)
					.put("ticks", ticks).get());

		final ReplicationDumpHeader header = toReplicationDumpHeader(res);
		boolean cont = handler.head(header);

		final StreamEntity entity = createEntity(res, StreamEntity.class);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(entity.getStream(), "utf-8"));
			String line = null;
			while (cont && (line = reader.readLine()) != null) {
				if (line.length() == 0) {
					continue;
				}
				cont = handler.handle(createEntity(line, ReplicationDumpRecord.class, clazz));
			}
		} catch (final UnsupportedEncodingException e) {
			throw new ArangoException("got UnsupportedEncodingException for utf-8", e);
		} catch (final IOException e) {
			throw new ArangoException(e);
		} finally {
			IOUtils.close(reader);
		}

	}

	@Override
	public ReplicationSyncEntity syncReplication(
		final String localDatabase,
		final String endpoint,
		final String database,
		final String username,
		final String password,
		final RestrictType restrictType,
		final String... restrictCollections) throws ArangoException {

		final HttpResponseEntity res = httpManager.doPut(createEndpointUrl(localDatabase, "/_api/replication/sync"),
			null,
			EntityFactory.toVPack(new MapBuilder().put("endpoint", endpoint).put("database", database)
					.put("username", username).put("password", password)
					.put("restrictType", restrictType == null ? null : restrictType.name().toLowerCase(Locale.US))
					.put("restrictCollections",
						restrictCollections == null || restrictCollections.length == 0 ? null : restrictCollections)
					.get()));

		return createEntity(res, ReplicationSyncEntity.class);

	}

	@Override
	public String getReplicationServerId() throws ArangoException {

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(null, "/_api/replication/server-id")); // MEMO:
		// not
		// use
		// database,
		// because
		// same
		// value
		// each
		// database.

		final MapAsEntity entity = createEntity(res, MapAsEntity.class);
		return (String) entity.getMap().get("serverId");

	}

	@Override
	public boolean startReplicationLogger(final String database) throws ArangoException {

		final HttpResponseEntity res = httpManager.doPut(createEndpointUrl(database, "/_api/replication/logger-start"),
			null, null);

		final MapAsEntity entity = createEntity(res, MapAsEntity.class);
		return (Boolean) entity.getMap().get("running");

	}

	@Override
	public boolean stopReplicationLogger(final String database) throws ArangoException {

		final HttpResponseEntity res = httpManager.doPut(createEndpointUrl(database, "/_api/replication/logger-stop"),
			null, null);

		final MapAsEntity entity = createEntity(res, MapAsEntity.class);
		return (Boolean) entity.getMap().get("running");

	}

	@Override
	public ReplicationLoggerConfigEntity getReplicationLoggerConfig(final String database) throws ArangoException {

		final HttpResponseEntity res = httpManager
				.doGet(createEndpointUrl(database, "/_api/replication/logger-config"));

		return createEntity(res, ReplicationLoggerConfigEntity.class);

	}

	@Override
	public ReplicationLoggerConfigEntity setReplicationLoggerConfig(
		final String database,
		final Boolean autoStart,
		final Boolean logRemoteChanges,
		final Long maxEvents,
		final Long maxEventsSize) throws ArangoException {

		final HttpResponseEntity res = httpManager.doPut(createEndpointUrl(database, "/_api/replication/logger-config"),
			null,
			EntityFactory.toVPack(new MapBuilder().put("autoStart", autoStart).put("logRemoteChanges", logRemoteChanges)
					.put("maxEvents", maxEvents).put("maxEventsSize", maxEventsSize).get()));

		return createEntity(res, ReplicationLoggerConfigEntity.class);

	}

	@Override
	public ReplicationLoggerStateEntity getReplicationLoggerState(final String database) throws ArangoException {

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(database, "/_api/replication/logger-state"));

		return createEntity(res, ReplicationLoggerStateEntity.class);

	}

	@Override
	public ReplicationApplierConfigEntity getReplicationApplierConfig(final String database) throws ArangoException {

		final HttpResponseEntity res = httpManager
				.doGet(createEndpointUrl(database, "/_api/replication/applier-config"));

		return createEntity(res, ReplicationApplierConfigEntity.class);

	}

	@Override
	public ReplicationApplierConfigEntity setReplicationApplierConfig(
		final String localDatabase,
		final String endpoint,
		final String database,
		final String username,
		final String password,
		final Integer maxConnectRetries,
		final Integer connectTimeout,
		final Integer requestTimeout,
		final Integer chunkSize,
		final Boolean autoStart,
		final Boolean adaptivePolling) throws ArangoException {

		final ReplicationApplierConfigEntity bodyParam = new ReplicationApplierConfigEntity();
		bodyParam.setEndpoint(endpoint);
		bodyParam.setDatabase(database);
		bodyParam.setUsername(username);
		bodyParam.setPassword(password);
		bodyParam.setMaxConnectRetries(maxConnectRetries);
		bodyParam.setConnectTimeout(connectTimeout);
		bodyParam.setRequestTimeout(requestTimeout);
		bodyParam.setChunkSize(chunkSize);
		bodyParam.setAutoStart(autoStart);
		bodyParam.setAdaptivePolling(adaptivePolling);

		return setReplicationApplierConfig(localDatabase, bodyParam);

	}

	@Override
	public ReplicationApplierConfigEntity setReplicationApplierConfig(
		final String database,
		final ReplicationApplierConfigEntity param) throws ArangoException {

		final HttpResponseEntity res = httpManager.doPut(
			createEndpointUrl(database, "/_api/replication/applier-config"), null, EntityFactory.toVPack(param));

		return createEntity(res, ReplicationApplierConfigEntity.class);

	}

	@Override
	public ReplicationApplierStateEntity startReplicationApplier(final String database, final Long from)
			throws ArangoException {

		final HttpResponseEntity res = httpManager.doPut(createEndpointUrl(database, "/_api/replication/applier-start"),
			new MapBuilder().put("from", from).get(), null);

		return createEntity(res, ReplicationApplierStateEntity.class);

	}

	@Override
	public ReplicationApplierStateEntity stopReplicationApplier(final String database) throws ArangoException {

		final HttpResponseEntity res = httpManager.doPut(createEndpointUrl(database, "/_api/replication/applier-stop"),
			null, null);

		return createEntity(res, ReplicationApplierStateEntity.class);

	}

	@Override
	public ReplicationApplierStateEntity getReplicationApplierState(final String database) throws ArangoException {

		final HttpResponseEntity res = httpManager
				.doGet(createEndpointUrl(database, "/_api/replication/applier-state"));

		return createEntity(res, ReplicationApplierStateEntity.class);

	}

}
