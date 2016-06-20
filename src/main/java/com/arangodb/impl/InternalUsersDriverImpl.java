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

import java.util.Map;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoException;
import com.arangodb.entity.DefaultEntity;
import com.arangodb.entity.EntityFactory;
import com.arangodb.entity.UserEntity;
import com.arangodb.entity.UsersEntity;
import com.arangodb.http.HttpManager;
import com.arangodb.http.HttpResponseEntity;
import com.arangodb.util.MapBuilder;
import com.arangodb.util.StringUtils;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * 
 */
public class InternalUsersDriverImpl extends BaseArangoDriverImpl implements com.arangodb.InternalUsersDriver {

	private static final String EXTRA = "extra";
	private static final String ACTIVE = "active";
	private static final String PW = "passwd";
	private static final String USERNAME = "user";
	private static final String GRANT = "grant";
	private static final String DATABASE = "database";
	private static final String READ_WRITE = "rw";

	InternalUsersDriverImpl(ArangoConfigure configure, HttpManager httpManager) {
		super(configure, httpManager);
	}

	@Override
	public DefaultEntity createUser(String username, String passwd, Boolean active, Map<String, Object> extra)
			throws ArangoException {

		HttpResponseEntity res = httpManager.doPost(createUserEndpointUrl(), null, EntityFactory.toJsonString(
			new MapBuilder().put(USERNAME, username).put(PW, passwd).put(ACTIVE, active).put(EXTRA, extra).get()));

		return createEntity(res, DefaultEntity.class);
	}

	@Override
	public DefaultEntity deleteUser(String username) throws ArangoException {

		HttpResponseEntity res = httpManager.doDelete(createUserEndpointUrl(StringUtils.encodeUrl(username)), null);

		return createEntity(res, DefaultEntity.class);
	}

	@Override
	public UserEntity getUser(String username) throws ArangoException {

		HttpResponseEntity res = httpManager.doGet(createUserEndpointUrl(StringUtils.encodeUrl(username)), null);

		return createEntity(res, UserEntity.class);
	}

	@Override
	public UsersEntity getUsers() throws ArangoException {

		HttpResponseEntity res = httpManager.doGet(createUserEndpointUrl(), null);

		return createEntity(res, UsersEntity.class);
	}

	@Override
	public DefaultEntity replaceUser(String username, String passwd, Boolean active, Map<String, Object> extra)
			throws ArangoException {

		HttpResponseEntity res = httpManager.doPut(createUserEndpointUrl(StringUtils.encodeUrl(username)), null,
			EntityFactory.toJsonString(new MapBuilder().put(PW, passwd).put(ACTIVE, active).put(EXTRA, extra).get()));

		return createEntity(res, DefaultEntity.class);
	}

	@Override
	public DefaultEntity updateUser(String username, String passwd, Boolean active, Map<String, Object> extra)
			throws ArangoException {

		HttpResponseEntity res = httpManager.doPatch(createUserEndpointUrl(StringUtils.encodeUrl(username)), null,
			EntityFactory.toJsonString(new MapBuilder().put(PW, passwd).put(ACTIVE, active).put(EXTRA, extra).get()));

		return createEntity(res, DefaultEntity.class);
	}

	@Override
	public DefaultEntity grantDatabaseAccess(String username, String database) throws ArangoException {

		final HttpResponseEntity res = httpManager.doPut(
			createUserEndpointUrl(StringUtils.encodeUrl(username), DATABASE, StringUtils.encodeUrl(database)), null,
			EntityFactory.toJsonString(new MapBuilder().put(GRANT, READ_WRITE).get()));

		return createEntity(res, DefaultEntity.class);
	}

}
