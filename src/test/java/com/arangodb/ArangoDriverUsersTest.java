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

package com.arangodb;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.arangodb.entity.DefaultEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.UserEntity;
import com.arangodb.util.MapBuilder;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * 
 */
public class ArangoDriverUsersTest extends BaseTest {

	@Before
	@After
	public void setup() throws ArangoException {
		// delete user
		for (final String user : new String[] { "user1", "user2", "user3", "user4", "testuser", "テスト☆ユーザー", "user-A",
				"userA", "userB", "ゆーざーA" }) {
			// user-A, userA, userB, ゆーざーA is created another(auth) testcase.
			try {
				driver.deleteUser(user);
			} catch (final ArangoException e) {
			}
		}
	}

	private Map<String, DocumentEntity<UserEntity>> toMap(final List<DocumentEntity<UserEntity>> users) {
		final TreeMap<String, DocumentEntity<UserEntity>> map = new TreeMap<String, DocumentEntity<UserEntity>>();
		for (final DocumentEntity<UserEntity> user : users) {
			map.put(user.getEntity().getUsername(), user);
		}
		return map;
	}

	@Test
	public void test_create_user() throws ArangoException {
		final DefaultEntity ret = driver.createUser("testuser", "test-pass1", null, null);

		assertThat(ret.getStatusCode(), is(201));
		assertThat(ret.getCode(), is(201));
		assertThat(ret.isError(), is(false));
	}

	@Test
	public void test_create_user_japanese() throws ArangoException {

		// create
		final DefaultEntity ret = driver.createUser("テスト☆ユーザー", "パスワード", null, null);
		// validate
		assertThat(ret.getStatusCode(), is(201));
		assertThat(ret.getCode(), is(201));
		assertThat(ret.isError(), is(false));

		// get
		final UserEntity user = driver.getUser("テスト☆ユーザー");
		assertThat(user.getUsername(), is("テスト☆ユーザー"));
		assertThat(user.getPassword(), is(nullValue()));
		assertThat(user.isActive(), is(true));
		assertThat(user.getExtra().isEmpty(), is(true));

	}

	@Test
	public void test_create_user_inactive() throws ArangoException {
		final DefaultEntity ret = driver.createUser("testuser", "test-pass1", false, null);

		assertThat(ret.getStatusCode(), is(201));
		assertThat(ret.getCode(), is(201));
		assertThat(ret.isError(), is(false));
	}

	@Test
	public void test_create_user_extra() throws ArangoException {

		// create user
		final DefaultEntity ret = driver.createUser("testuser", "test-pass1", false,
			new MapBuilder().put("attr1", "寿司").put("日本語属性1", "日本語値").get());

		// valdate
		assertThat(ret.getStatusCode(), is(201));
		assertThat(ret.getCode(), is(201));
		assertThat(ret.isError(), is(false));

		// get user
		final UserEntity user = driver.getUser("testuser");
		assertThat(user.getUsername(), is("testuser"));
		assertThat(user.getPassword(), is(nullValue()));
		assertThat(user.isActive(), is(false));
		assertThat(user.getExtra().size(), is(2));
		assertThat((String) user.getExtra().get("attr1"), is("寿司"));
		assertThat((String) user.getExtra().get("日本語属性1"), is("日本語値"));

	}

	@Test
	public void test_create_user_duplicate() throws ArangoException {

		final DefaultEntity ret = driver.createUser("testuser", "test-pass1", null, null);
		assertThat(ret.isError(), is(false));

		try {
			driver.createUser("testuser", "test-pass1", null, null);
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1702));
			assertThat(e.getMessage(), containsString("duplicate user"));
		}

	}

	@Test
	public void test_create_user_empty() throws ArangoException {

		try {
			driver.createUser("", "test-pass1", null, null);
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1700));
			assertThat(e.getMessage(), containsString("invalid user name"));
		}

	}

	@Test
	public void test_delete_user_empty() throws ArangoException {

		try {
			driver.deleteUser("");
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(400));
			assertThat(e.getMessage(), containsString("bad parameter"));
		}

	}

	public void test_delete_user_404() throws ArangoException {

		try {
			driver.deleteUser("testuser");
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1703));
			assertThat(e.getMessage(), containsString("user not found"));
		}

	}

	@Test
	public void test_get_user_empty() throws ArangoException {
		// returns a list of users

		// TODO check the list
		driver.getUser("");

	}

	@Test
	public void test_get_user_404() throws ArangoException {

		try {
			driver.getUser("testuser");
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1703));
			assertThat(e.getMessage(), containsString("user not found"));
		}

	}

	@Test
	public void test_replace_user() throws ArangoException {

		// create
		driver.createUser("testuser", "pass1", true, null);
		// get document
		final DocumentEntity<UserEntity> doc1 = toMap(driver.getUsersDocument()).get("testuser");

		// replace
		final DefaultEntity res = driver.replaceUser("testuser", "pass2", false,
			new MapBuilder().put("aaa", "bbbb").get());
		assertThat(res.getCode(), is(200));
		assertThat(res.isError(), is(false));

		// get replace user
		final DocumentEntity<UserEntity> doc2 = toMap(driver.getUsersDocument()).get("testuser");

		assertThat(doc2.getEntity().getUsername(), is("testuser"));
		// assertThat(doc2.getEntity().getPassword(),
		// is(not(doc1.getEntity().getPassword())));
		assertThat(doc2.getEntity().isActive(), is(false));
		assertThat((String) doc2.getEntity().getExtra().get("aaa"), is("bbbb"));

		assertThat(doc2.getDocumentRevision(), greaterThan(doc1.getDocumentRevision()));
		assertThat(doc2.getDocumentKey(), is(doc1.getDocumentKey()));
		assertThat(doc2.getDocumentHandle(), is(doc1.getDocumentHandle()));

	}

	@Test
	public void test_replace_user_empty() throws ArangoException {

		try {
			driver.replaceUser("", "pass2", false, new MapBuilder().put("aaa", "bbbb").get());
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(400));
			assertThat(e.getMessage(), containsString("bad parameter"));
		}

	}

	@Test
	public void test_replace_user_404() throws ArangoException {

		try {
			driver.replaceUser("testuser", "pass2", false, new MapBuilder().put("aaa", "bbbb").get());
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1703));
			assertThat(e.getMessage(), containsString("user not found"));
		}

	}

	@Test
	public void test_update_user() throws ArangoException {

		// create
		driver.createUser("testuser", "pass1", true, null);
		// get document
		final DocumentEntity<UserEntity> doc1 = toMap(driver.getUsersDocument()).get("testuser");

		// partial update
		final DefaultEntity res = driver.updateUser("testuser", null, null, new MapBuilder().put("aaa", "bbbb").get());
		assertThat(res.getCode(), is(200));
		assertThat(res.isError(), is(false));

		// get replace user
		final DocumentEntity<UserEntity> doc2 = toMap(driver.getUsersDocument()).get("testuser");

		assertThat(doc2.getEntity().getUsername(), is("testuser"));
		assertThat(doc2.getEntity().getPassword(), is(doc1.getEntity().getPassword()));
		assertThat(doc2.getEntity().isActive(), is(true));
		assertThat((String) doc2.getEntity().getExtra().get("aaa"), is("bbbb"));

		assertThat(doc2.getDocumentRevision(), greaterThan(doc1.getDocumentRevision()));
		assertThat(doc2.getDocumentKey(), is(doc1.getDocumentKey()));
		assertThat(doc2.getDocumentHandle(), is(doc1.getDocumentHandle()));

	}

	@Test
	public void test_update_user_empty() throws ArangoException {

		try {
			driver.updateUser("", "pass2", false, new MapBuilder().put("aaa", "bbbb").get());
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(400));
			assertThat(e.getMessage(), containsString("bad parameter"));
		}

	}

	@Test
	public void test_update_user_404() throws ArangoException {

		try {
			driver.updateUser("testuser", "pass2", false, new MapBuilder().put("aaa", "bbbb").get());
			fail("did not raise exception");
		} catch (final ArangoException e) {
			assertThat(e.getErrorNumber(), is(1703));
			assertThat(e.getMessage(), containsString("user not found"));
		}

	}

	@Test
	public void test_get_users() throws ArangoException {

		int expectedUserSize = driver.getUsers().size() + 3;

		driver.createUser("user1", "pass1", true, null);
		driver.createUser("user2", "pass2", false, null);
		driver.createUser("user3", "pass3", true, new MapBuilder().put("key", "value").get());

		// get users
		final List<UserEntity> users = driver.getUsers();
		Collections.sort(users, new Comparator<UserEntity>() {
			@Override
			public int compare(final UserEntity o1, final UserEntity o2) {
				return o1.getUsername().compareTo(o2.getUsername());
			}
		});

		// validate
		assertThat(users.size(), is(expectedUserSize)); // user1,2,3 and root
		assertThat(users.get(0).getUsername(), is("root"));

		assertThat(users.get(1).getUsername(), is("user1"));
		assertThat(users.get(1).isActive(), is(true));
		assertThat(users.get(1).getExtra(), is(notNullValue()));
		assertThat(users.get(1).getExtra().size(), is(0));

		assertThat(users.get(2).getUsername(), is("user2"));
		assertThat(users.get(2).isActive(), is(false));
		assertThat(users.get(2).getExtra(), is(notNullValue()));
		assertThat(users.get(2).getExtra().size(), is(0));

		assertThat(users.get(3).getUsername(), is("user3"));
		assertThat(users.get(3).isActive(), is(true));
		assertThat(users.get(3).getExtra().size(), is(1));
		assertThat((String) users.get(3).getExtra().get("key"), is("value"));

	}

}
