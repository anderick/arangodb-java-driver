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

package com.arangodb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.arangodb.entity.AqlFunctionsEntity;
import com.arangodb.entity.CollectionEntity;
import com.arangodb.entity.DefaultEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverAqlfunctionsTest extends BaseTest {

	@Before
	public void before() throws ArangoException {
		final AqlFunctionsEntity res = driver.getAqlFunctions(null);
		final Iterator<String> it = res.getAqlFunctions().keySet().iterator();
		while (it.hasNext()) {
			driver.deleteAqlFunction(it.next(), false);
		}
	}

	@After
	public void after() {
	}

	@Test
	public void test_AqlFunctions() throws ArangoException {
		{
			final CollectionEntity count = driver.getCollectionCount("_aqlfunctions");
			Assert.assertEquals(0, count.getCount());
		}

		DefaultEntity res = driver.createAqlFunction("someNamespace::testCode",
			"function (celsius) { return celsius * 2.8 + 32; }");
		assertThat(res.getCode(), is(201));
		assertThat(res.getErrorMessage(), is((String) null));

		try {
			res = driver.createAqlFunction("someNamespace::testC&&&&&&&&&&de",
				"function (celsius) { return celsius * 2.8 + 32; }");
		} catch (final ArangoException e) {
			assertThat(e.getCode(), is(400));
			assertThat(e.getErrorMessage(), is("invalid user function name"));
		}

		res = driver.createAqlFunction("anotherNamespace::testCode",
			"function (celsius) { return celsius * 2.8 + 32; }");
		assertThat(res.getCode(), is(201));
		assertThat(res.getErrorMessage(), is((String) null));
		res = driver.createAqlFunction("anotherNamespace::testCode2",
			"function (celsius) { return celsius * 2.8 + 32; }");
		assertThat(res.getCode(), is(201));
		assertThat(res.getErrorMessage(), is((String) null));

		{
			final CollectionEntity count = driver.getCollectionCount("_aqlfunctions");
			Assert.assertEquals(3, count.getCount());
		}

		AqlFunctionsEntity r = driver.getAqlFunctions(null);
		assertThat(r.size(), is(3));
		assertTrue(r.getAqlFunctions().keySet().contains("anotherNamespace::testCode"));
		assertTrue(r.getAqlFunctions().keySet().contains("someNamespace::testCode"));

		r = driver.getAqlFunctions("someNamespace");
		assertThat(r.size(), is(1));
		assertFalse(r.getAqlFunctions().keySet().contains("anotherNamespace::testCode"));
		assertTrue(r.getAqlFunctions().keySet().contains("someNamespace::testCode"));

		res = driver.deleteAqlFunction("someNamespace::testCode", false);
		assertThat(res.getCode(), is(200));
		assertThat(res.getErrorMessage(), is((String) null));

		res = driver.deleteAqlFunction("anotherNamespace", true);
		assertThat(res.getCode(), is(200));
		assertThat(res.getErrorMessage(), is((String) null));

		final AqlFunctionsEntity c = driver.getAqlFunctions("someNamespace");
		assertThat(c.size(), is(0));
	}

}
