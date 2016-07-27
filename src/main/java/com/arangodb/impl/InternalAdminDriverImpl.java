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

package com.arangodb.impl;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoException;
import com.arangodb.entity.AdminLogEntity;
import com.arangodb.entity.ArangoUnixTime;
import com.arangodb.entity.ArangoVersion;
import com.arangodb.entity.DefaultEntity;
import com.arangodb.entity.StatisticsDescriptionEntity;
import com.arangodb.entity.StatisticsEntity;
import com.arangodb.http.HttpManager;
import com.arangodb.http.HttpResponseEntity;
import com.arangodb.util.MapBuilder;
import com.arangodb.velocypack.VPackSlice;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class InternalAdminDriverImpl extends BaseArangoDriverImpl implements com.arangodb.InternalAdminDriver {

	// MEMO: ADMINはdatabase関係ない

	InternalAdminDriverImpl(final ArangoConfigure configure, final HttpManager httpManager) {
		super(configure, httpManager);
	}

	@Override
	public AdminLogEntity getServerLog(
		final Integer logLevel,
		final Boolean logLevelUpTo,
		final Integer start,
		final Integer size,
		final Integer offset,
		final Boolean sortAsc,
		final String text) throws ArangoException {

		// パラメータを作る
		final MapBuilder param = new MapBuilder();
		if (logLevel != null) {
			if (logLevelUpTo != null && logLevelUpTo.booleanValue()) {
				param.put("upto", logLevel);
			} else {
				param.put("level", logLevel);
			}
		}
		param.put("start", start);
		param.put("size", size);
		param.put("offset", offset);
		if (sortAsc != null) {
			param.put("sort", sortAsc.booleanValue() ? "asc" : "desc");
		}
		param.put("search", text);

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(null, "/_admin/log"), param.get());

		return createEntity(res, AdminLogEntity.class);
	}

	@Override
	public StatisticsEntity getStatistics() throws ArangoException {

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(null, "/_admin/statistics"));

		return createEntity(res, StatisticsEntity.class);
	}

	@Override
	public StatisticsDescriptionEntity getStatisticsDescription() throws ArangoException {

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(null, "/_admin/statistics-description"));

		return createEntity(res, StatisticsDescriptionEntity.class);
	}

	/**
	 * Returns the ArangoDB version
	 * 
	 * @return a ArangoVersion object
	 * @throws ArangoException
	 * @see <a href=
	 *      "http://www.arangodb.com/manuals/current/HttpMisc.html#HttpMiscVersion">
	 *      HttpMiscVersion documentation</a>
	 */
	@Override
	public ArangoVersion getVersion() throws ArangoException {

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(null, "/_api/version"));

		return createEntity(res, ArangoVersion.class);
	}

	@Override
	public ArangoUnixTime getTime() throws ArangoException {

		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(null, "/_admin/time"));

		return createEntity(res, ArangoUnixTime.class);
	}

	@Override
	public DefaultEntity reloadRouting() throws ArangoException {

		final HttpResponseEntity res = httpManager.doPost(createEndpointUrl(null, "/_admin/routing/reload"), null,
			(VPackSlice) null);

		return createEntity(res, DefaultEntity.class, null, false);
	}

	@Override
	public DefaultEntity executeScript(final String database, final String jsCode) throws ArangoException {

		final HttpResponseEntity res = httpManager.doPost(createEndpointUrl(database, "/_admin/execute"), null, jsCode);

		return createEntity(res, DefaultEntity.class);
	}

}
