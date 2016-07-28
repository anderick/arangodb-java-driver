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

import java.util.Collection;
import java.util.Map;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoException;
import com.arangodb.entity.EntityFactory;
import com.arangodb.entity.ImportResultEntity;
import com.arangodb.http.HttpManager;
import com.arangodb.http.HttpResponseEntity;
import com.arangodb.util.ImportOptions;
import com.arangodb.util.ImportOptionsJson;
import com.arangodb.util.ImportOptionsRaw;
import com.arangodb.velocypack.VPackSlice;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @see <a href=
 *      "https://docs.arangodb.com/HttpBulkImports/ImportingSelfContained.html">
 *      HttpBulkImports documentation</a>
 */
public class InternalImportDriverImpl extends BaseArangoDriverImpl implements com.arangodb.InternalImportDriver {

	InternalImportDriverImpl(final ArangoConfigure configure, final HttpManager httpManager) {
		super(configure, httpManager);
	}

	@Override
	public ImportResultEntity importDocuments(
		final String database,
		final String collection,
		final Collection<?> values,
		final ImportOptionsJson importOptionsJson) throws ArangoException {

		final Map<String, Object> map = importOptionsJson.toMap();
		map.put("type", "list");
		final VPackSlice obj = EntityFactory.toVPack(values);
		return importDocumentsInternal(database, collection, obj, map);
	}

	@Override
	public ImportResultEntity importDocumentsRaw(
		final String database,
		final String collection,
		final String values,
		final ImportOptionsRaw importOptionsRaw) throws ArangoException {

		final VPackSlice obj = EntityFactory.toVPack(values);
		return importDocumentsInternal(database, collection, obj, importOptionsRaw.toMap());
	}

	@Override
	public ImportResultEntity importDocumentsByHeaderValues(
		final String database,
		final String collection,
		final Collection<? extends Collection<?>> headerValues,
		final ImportOptions importOptions) throws ArangoException {

		return importDocumentsByHeaderValuesInternal(database, collection,
			EntityFactory.toImportHeaderValues(headerValues), importOptions);
	}

	@Override
	public ImportResultEntity importDocumentsByHeaderValuesRaw(
		final String database,
		final String collection,
		final String headerValues,
		final ImportOptions importOptions) throws ArangoException {

		final VPackSlice obj = EntityFactory.toVPack(headerValues);
		return importDocumentsByHeaderValuesInternal(database, collection, obj, importOptions);
	}

	private ImportResultEntity importDocumentsInternal(
		final String database,
		final String collection,
		final VPackSlice values,
		final Map<String, Object> importOptions) throws ArangoException {

		importOptions.put(COLLECTION, collection);

		final HttpResponseEntity res = httpManager.doPost(createEndpointUrl(database, "/_api/import"), importOptions,
			values);

		return createEntity(res, ImportResultEntity.class);
	}

	private ImportResultEntity importDocumentsByHeaderValuesInternal(
		final String database,
		final String collection,
		final VPackSlice headerValues,
		final ImportOptions importOptions) throws ArangoException {

		final Map<String, Object> map = importOptions.toMap();
		map.put(COLLECTION, collection);

		final HttpResponseEntity res = httpManager.doPost(createEndpointUrl(database, "/_api/import"), map,
			headerValues);

		return createEntity(res, ImportResultEntity.class);

	}

}
