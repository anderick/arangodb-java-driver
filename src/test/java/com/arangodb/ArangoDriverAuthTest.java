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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;

/**
 * Basic Auth test. Must "disable-authentication=no" in server configure.
 * 
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverAuthTest {

	private static final String USER_MIN_A = "user-A";
	private static final String USER_A = "userA";
	private static final String USER_B = "userB";
	private static final String USER_JAP_A = "ゆーざーA";

	@After
	public void after() {
		ArangoConfigure configure = new ArangoConfigure();
		configure.init();
		ArangoDriver driver = new ArangoDriver(configure);
		for (String username : new String[] { USER_A, USER_B, USER_MIN_A, USER_JAP_A }) {
			try {
				driver.deleteUser(username);
			} catch (ArangoException e) {
			}
		}
	}

	@Test
	public void test_auth() throws ArangoException {

		ArangoConfigure configure = new ArangoConfigure();
		configure.setUser(null);
		configure.setPassword(null);
		configure.init();

		ArangoDriver driver = new ArangoDriver(configure);
		try {
			driver.getTime();
			fail();
		} catch (ArangoException e) {
			assertThat(e.isUnauthorized(), is(true));
			assertThat(e.getEntity().getStatusCode(), is(401));
			assertThat(e.getErrorMessage(), containsString("Unauthorized"));
		}

		configure.shutdown();
	}

	@Test
	public void test_auth_root() throws ArangoException {

		ArangoConfigure configure = new ArangoConfigure();
		configure.setUser("root");
		configure.setPassword("");
		configure.init();

		ArangoDriver driver = new ArangoDriver(configure);
		driver.getTime();

		configure.shutdown();
	}

	@Test
	public void test_auth_added_user() throws ArangoException {

		ArangoConfigure configure = new ArangoConfigure();
		configure.setUser("root");
		configure.setPassword("");
		configure.init();

		ArangoDriver driver = new ArangoDriver(configure);

		// Create User
		final String username = USER_A;
		try {
			driver.createUser(username, "passA", true, null);
		} catch (ArangoException e) {
			driver.replaceUser(username, "passA", true, null);
		}
		driver.grantDatabaseAccess(username, "_system");
		configure.shutdown();

		configure = new ArangoConfigure();
		configure.setUser(username);
		configure.setPassword("passA");
		configure.init();
		driver = new ArangoDriver(configure);
		driver.getTime();
		configure.shutdown();

	}

	@Test
	public void test_auth_added_user_inactive() throws ArangoException {

		ArangoConfigure configure = new ArangoConfigure();
		configure.setUser("root");
		configure.setPassword("");
		configure.init();

		ArangoDriver driver = new ArangoDriver(configure);

		// Create User
		try {
			driver.createUser(USER_B, "passB", false, null);
		} catch (ArangoException e) {
			driver.replaceUser(USER_B, "passB", false, null);
		}

		configure.shutdown();

		configure = new ArangoConfigure();
		configure.setUser(USER_B);
		configure.setPassword("passB");
		configure.init();
		driver = new ArangoDriver(configure);

		// Memo: Failed version 1.2.3
		try {
			driver.getTime();
			fail("");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(401));
			assertThat(e.getErrorNumber(), is(0));
			assertThat(e.getErrorMessage(), containsString("Unauthorized"));
		}
		configure.shutdown();

	}

	@Test
	public void test_auth_multibyte_username() throws ArangoException {

		ArangoConfigure configure = new ArangoConfigure();
		configure.setUser("root");
		configure.setPassword("");
		configure.init();

		ArangoDriver driver = new ArangoDriver(configure);

		// Create User
		try {
			driver.createUser(USER_JAP_A, "pass", false, null);
		} catch (ArangoException e) {
			driver.replaceUser(USER_JAP_A, "pass", false, null);
		}

		configure.shutdown();

		ArangoConfigure configure2 = new ArangoConfigure();
		configure2.setUser(USER_JAP_A);
		configure2.setPassword("pass");
		configure2.init();
		ArangoDriver driver2 = new ArangoDriver(configure2);

		try {
			driver2.getTime();
			fail("");
		} catch (ArangoException e) {
			assertThat(e.getCode(), is(401));
			assertThat(e.getErrorNumber(), is(0));
			assertThat(e.getErrorMessage(), containsString("Unauthorized"));
		}

		configure2.shutdown();
	}

	@Test
	public void test_auth_multibyte_password() throws ArangoException {

		ArangoConfigure configure = new ArangoConfigure();
		configure.setUser("root");
		configure.setPassword("");
		configure.init();

		ArangoDriver driver = new ArangoDriver(configure);

		// Create User
		try {
			driver.createUser(USER_MIN_A, "パスワード", false, null);
		} catch (ArangoException e) {
			driver.replaceUser(USER_MIN_A, "パスワード", false, null);
		}

		configure.shutdown();

		configure = new ArangoConfigure();
		configure.setUser(USER_MIN_A);
		configure.setPassword("パスワード");
		configure.init();
		driver = new ArangoDriver(configure);

		try {
			driver.getTime();
			fail("");
		} catch (ArangoException e) {
			assertThat(e.getErrorNumber(), is(0));
			assertThat(e.getCode(), is(401));
			assertThat(e.getErrorMessage(), containsString("Unauthorized"));
		}
		configure.shutdown();

	}

}
