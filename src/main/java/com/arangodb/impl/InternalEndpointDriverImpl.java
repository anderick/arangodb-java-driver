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

import java.util.List;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoException;
import com.arangodb.entity.BooleanResultEntity;
import com.arangodb.entity.Endpoint;
import com.arangodb.entity.EntityFactory;
import com.arangodb.http.HttpManager;
import com.arangodb.http.HttpResponseEntity;
import com.arangodb.util.MapBuilder;
import com.arangodb.util.StringUtils;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @since 1.4
 */
public class InternalEndpointDriverImpl extends BaseArangoDriverImpl implements com.arangodb.InternalEndpointDriver {

	private static final String API_ENDPOINT = "/_api/endpoint";

	InternalEndpointDriverImpl(final ArangoConfigure configure, final HttpManager httpManager) {
		super(configure, httpManager);
	}

	@Override
	public BooleanResultEntity createEndpoint(final String endpoint, final String... databases) throws ArangoException {
		// TODO: validate endpoint
		// validate databases
		if (databases != null) {
			for (final String db : databases) {
				validateDatabaseName(db, false);
			}
		}
		final HttpResponseEntity res = httpManager.doPost(createEndpointUrl(null, API_ENDPOINT), null,
			EntityFactory.toVPack(new MapBuilder().put("endpoint", endpoint).put("databases", databases).get()));
		return createEntity(res, BooleanResultEntity.class);

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Endpoint> getEndpoints() throws ArangoException {
		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(null, API_ENDPOINT));
		// because it is not include common-attribute.
		return EntityFactory.createEntity(res.getContent(), List.class, Endpoint.class);

	}

	@Override
	public BooleanResultEntity deleteEndpoint(final String endpoint) throws ArangoException {
		// TODO: validate endpoint
		final HttpResponseEntity res = httpManager
				.doDelete(createEndpointUrl(null, API_ENDPOINT, StringUtils.encodeUrl(endpoint)), null);
		return createEntity(res, BooleanResultEntity.class);
	}

}
