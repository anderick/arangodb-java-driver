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
import com.arangodb.entity.AqlFunctionsEntity;
import com.arangodb.entity.DefaultEntity;
import com.arangodb.entity.EntityFactory;
import com.arangodb.http.HttpManager;
import com.arangodb.http.HttpResponseEntity;
import com.arangodb.util.MapBuilder;

/**
 * @author Florian Bartels
 *
 */
public class InternalAqlFunctionsDriverImpl extends BaseArangoDriverImpl
		implements com.arangodb.InternalAqlFunctionsDriver {

	private static final String API_AQLFUNCTION = "/_api/aqlfunction";

	InternalAqlFunctionsDriverImpl(final ArangoConfigure configure, final HttpManager httpManager) {
		super(configure, httpManager);
	}

	@Override
	public DefaultEntity createAqlFunction(final String database, final String name, final String code)
			throws ArangoException {
		final HttpResponseEntity res = httpManager.doPost(createEndpointUrl(database, API_AQLFUNCTION), null,
			EntityFactory.toVPack(new MapBuilder().put("name", name).put("code", code).get()));
		return createEntity(res, DefaultEntity.class, null, false);
	}

	@Override
	public AqlFunctionsEntity getAqlFunctions(final String database, final String namespace) throws ArangoException {

		String appendix = "";
		if (namespace != null) {
			appendix = "?namespace=" + namespace;
		}
		final HttpResponseEntity res = httpManager.doGet(createEndpointUrl(database, API_AQLFUNCTION + appendix));
		return createEntity(res, AqlFunctionsEntity.class);

	}

	@Override
	public DefaultEntity deleteAqlFunction(final String database, final String name, final boolean isNameSpace)
			throws ArangoException {

		final HttpResponseEntity res = httpManager.doDelete(createEndpointUrl(database, API_AQLFUNCTION, name),
			new MapBuilder().put("group", isNameSpace).get());

		return createEntity(res, DefaultEntity.class);

	}

}
