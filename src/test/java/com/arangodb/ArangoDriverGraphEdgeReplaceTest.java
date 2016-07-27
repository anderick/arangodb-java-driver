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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.EdgeEntity;
import com.arangodb.entity.marker.VertexEntity;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoDriverGraphEdgeReplaceTest extends BaseGraphTest {

	private final String graphName = "UnitTestGraph";
	private final String edgeCollectionName = "edge-1";

	@Test
	public void test_replace_edge() throws ArangoException {

		driver.createGraph(this.graphName, this.createEdgeDefinitions(2, 0), this.createOrphanCollections(2), true);
		final VertexEntity<TestComplexEntity01> v1 = driver.graphCreateVertex(this.graphName, "from1-1",
			new TestComplexEntity01("v1-user", "desc1", 10), null);
		final VertexEntity<TestComplexEntity01> v2 = driver.graphCreateVertex(this.graphName, "to1-1",
			new TestComplexEntity01("v2-user", "desc2", 12), null);
		driver.graphCreateVertex(this.graphName, "from1-1", new TestComplexEntity01("v3-user", "desc3", 14), null);
		driver.graphCreateVertex(this.graphName, "to1-1", new TestComplexEntity01("v4-user", "desc4", 20), null);

		final EdgeEntity<?> edge = driver.graphCreateEdge(this.graphName, this.edgeCollectionName, null,
			v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity02(1, 2, 3), null);
		assertThat(edge.getCode(), is(202));

		EdgeEntity<TestComplexEntity01> replacedEdge = driver.graphReplaceEdge(this.graphName, this.edgeCollectionName,
			edge.getDocumentKey(), v1.getDocumentHandle(), v2.getDocumentHandle(),
			new TestComplexEntity01("xx", "yy", 20));
		assertThat(replacedEdge.getCode(), is(202));
		assertThat(replacedEdge.isError(), is(false));
		assertThat(replacedEdge.getDocumentKey(), is(edge.getDocumentKey()));
		assertThat(replacedEdge.getDocumentRevision(), is(not(edge.getDocumentRevision())));
		assertThat(replacedEdge.getDocumentHandle(), is(edge.getDocumentHandle()));
		replacedEdge = driver.graphGetEdge(this.graphName, this.edgeCollectionName, replacedEdge.getDocumentKey(),
			TestComplexEntity01.class);
		assertThat(replacedEdge.getFromVertexHandle(), is(v1.getDocumentHandle()));
		assertThat(replacedEdge.getToVertexHandle(), is(v2.getDocumentHandle()));

		assertThat(replacedEdge.getEntity(), instanceOf(TestComplexEntity01.class));
		assertThat(replacedEdge.getEntity().getUser(), is("xx"));
		assertThat(replacedEdge.getEntity().getDesc(), is("yy"));
		assertThat(replacedEdge.getEntity().getAge(), is(20));

	}

	@Test
	public void test_replace_edge_null() throws ArangoException {

		driver.createGraph(this.graphName, this.createEdgeDefinitions(2, 0), this.createOrphanCollections(2), true);
		final DocumentEntity<TestComplexEntity01> v1 = driver.graphCreateVertex(this.graphName, "from1-1",
			new TestComplexEntity01("v1-user", "desc1", 10), null);
		final VertexEntity<TestComplexEntity01> v2 = driver.graphCreateVertex(this.graphName, "to1-1",
			new TestComplexEntity01("v2-user", "desc2", 12), null);
		driver.graphCreateVertex(this.graphName, "from1-1", new TestComplexEntity01("v3-user", "desc3", 14), null);
		driver.graphCreateVertex(this.graphName, "to1-1", new TestComplexEntity01("v4-user", "desc4", 20), null);

		final EdgeEntity<?> edge = driver.graphCreateEdge(this.graphName, this.edgeCollectionName, null,
			v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity02(1, 2, 3), null);
		assertThat(edge.getCode(), is(202));

		EdgeEntity<?> replacedEdge = driver.graphReplaceEdge(this.graphName, this.edgeCollectionName,
			edge.getDocumentKey(), v1.getDocumentHandle(), v2.getDocumentHandle(), null);
		assertThat(replacedEdge.getCode(), is(202));
		assertThat(replacedEdge.isError(), is(false));
		assertThat(replacedEdge.getDocumentKey(), is(edge.getDocumentKey()));
		assertThat(replacedEdge.getDocumentRevision(), is(not(edge.getDocumentRevision())));
		assertThat(replacedEdge.getDocumentHandle(), is(edge.getDocumentHandle()));

		replacedEdge = driver.graphGetEdge(this.graphName, this.edgeCollectionName, replacedEdge.getDocumentKey(),
			null);

		assertThat(replacedEdge.getFromVertexHandle(), is(v1.getDocumentHandle()));
		assertThat(replacedEdge.getToVertexHandle(), is(v2.getDocumentHandle()));
		assertThat(replacedEdge.getEntity(), is(nullValue()));

	}

	@Test
	public void test_replace_edge_waitForSync() throws ArangoException {

		driver.createGraph(this.graphName, this.createEdgeDefinitions(2, 0), this.createOrphanCollections(2), true);
		final VertexEntity<TestComplexEntity01> v1 = driver.graphCreateVertex(this.graphName, "from1-1",
			new TestComplexEntity01("v1-user", "desc1", 10), null);
		final VertexEntity<TestComplexEntity01> v2 = driver.graphCreateVertex(this.graphName, "to1-1",
			new TestComplexEntity01("v2-user", "desc2", 12), null);
		driver.graphCreateVertex(this.graphName, "from1-1", new TestComplexEntity01("v3-user", "desc3", 14), null);
		driver.graphCreateVertex(this.graphName, "to1-1", new TestComplexEntity01("v4-user", "desc4", 20), null);

		final EdgeEntity<?> edge = driver.graphCreateEdge(this.graphName, this.edgeCollectionName, null,
			v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity02(1, 2, 3), null);
		assertThat(edge.getCode(), is(202));

		EdgeEntity<?> replacedEdge = driver.graphReplaceEdge(this.graphName, this.edgeCollectionName,
			edge.getDocumentKey(), v1.getDocumentHandle(), v2.getDocumentHandle(), null, false, null, null);
		assertThat(replacedEdge.getCode(), is(202));
		assertThat(replacedEdge.isError(), is(false));
		assertThat(replacedEdge.getDocumentKey(), is(edge.getDocumentKey()));
		assertThat(replacedEdge.getDocumentRevision(), is(not(edge.getDocumentRevision())));
		assertThat(replacedEdge.getDocumentHandle(), is(edge.getDocumentHandle()));
		replacedEdge = driver.graphGetEdge(this.graphName, this.edgeCollectionName, edge.getDocumentKey(), null);
		assertThat(replacedEdge.getFromVertexHandle(), is(v1.getDocumentHandle()));
		assertThat(replacedEdge.getToVertexHandle(), is(v2.getDocumentHandle()));
		assertThat(replacedEdge.getEntity(), is(nullValue()));

	}

	@Test
	public void test_replace_edge_rev_eq() throws ArangoException {

		driver.createGraph(this.graphName, this.createEdgeDefinitions(2, 0), this.createOrphanCollections(2), true);
		final DocumentEntity<TestComplexEntity01> v1 = driver.graphCreateVertex(this.graphName, "from1-1",
			new TestComplexEntity01("v1-user", "desc1", 10), null);
		final VertexEntity<TestComplexEntity01> v2 = driver.graphCreateVertex(this.graphName, "to1-1",
			new TestComplexEntity01("v2-user", "desc2", 12), null);
		driver.graphCreateVertex(this.graphName, "from1-1", new TestComplexEntity01("v3-user", "desc3", 14), null);
		driver.graphCreateVertex(this.graphName, "to1-1", new TestComplexEntity01("v4-user", "desc4", 20), null);

		final EdgeEntity<?> edge = driver.graphCreateEdge(this.graphName, this.edgeCollectionName, null,
			v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity02(1, 2, 3), null);
		assertThat(edge.getCode(), is(202));

		final String rev = edge.getDocumentRevision();
		EdgeEntity<TestComplexEntity01> replacedEdge = driver.graphReplaceEdge(this.graphName, this.edgeCollectionName,
			edge.getDocumentKey(), v1.getDocumentHandle(), v2.getDocumentHandle(),
			new TestComplexEntity01("xx", "yy", 20), null, rev, null);
		assertThat(replacedEdge.getCode(), is(202));
		assertThat(replacedEdge.isError(), is(false));
		assertThat(replacedEdge.getDocumentKey(), is(edge.getDocumentKey()));
		assertThat(replacedEdge.getDocumentRevision(), is(not(edge.getDocumentRevision())));
		assertThat(replacedEdge.getDocumentHandle(), is(edge.getDocumentHandle()));
		replacedEdge = driver.graphGetEdge(this.graphName, this.edgeCollectionName, edge.getDocumentKey(),
			TestComplexEntity01.class);
		assertThat(replacedEdge.getFromVertexHandle(), is(v1.getDocumentHandle()));
		assertThat(replacedEdge.getToVertexHandle(), is(v2.getDocumentHandle()));
		assertThat(replacedEdge.getEntity(), instanceOf(TestComplexEntity01.class));
		assertThat(replacedEdge.getEntity().getUser(), is("xx"));
		assertThat(replacedEdge.getEntity().getDesc(), is("yy"));
		assertThat(replacedEdge.getEntity().getAge(), is(20));

	}

	@Test
	public void test_replace_edge_rev_ne() throws ArangoException {

		driver.createGraph(this.graphName, this.createEdgeDefinitions(2, 0), this.createOrphanCollections(2), true);
		final VertexEntity<TestComplexEntity01> v1 = driver.graphCreateVertex(this.graphName, "from1-1",
			new TestComplexEntity01("v1-user", "desc1", 10), null);
		final VertexEntity<TestComplexEntity01> v2 = driver.graphCreateVertex(this.graphName, "to1-1",
			new TestComplexEntity01("v2-user", "desc2", 12), null);
		driver.graphCreateVertex(this.graphName, "from1-1", new TestComplexEntity01("v3-user", "desc3", 14), null);
		driver.graphCreateVertex(this.graphName, "to1-1", new TestComplexEntity01("v4-user", "desc4", 20), null);

		final EdgeEntity<?> edge = driver.graphCreateEdge(this.graphName, this.edgeCollectionName, null,
			v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity02(1, 2, 3), null);
		assertThat(edge.getCode(), is(202));

		try {
			final String rev = edge.getDocumentRevision() + 1;
			driver.graphReplaceEdge(this.graphName, this.edgeCollectionName, edge.getDocumentKey(),
				v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity01("xx", "yy", 20), null, rev,
				null);
			fail();
		} catch (final ArangoException e) {
			assertThat(e.getCode(), is(412));
			assertThat(e.getErrorNumber(), is(1903));
			assertThat(e.getErrorMessage(), is("wrong revision"));
		}

	}

	@Test
	public void test_replace_edge_match_eq() throws ArangoException {

		driver.createGraph(this.graphName, this.createEdgeDefinitions(2, 0), this.createOrphanCollections(2), true);
		final VertexEntity<TestComplexEntity01> v1 = driver.graphCreateVertex(this.graphName, "from1-1",
			new TestComplexEntity01("v1-user", "desc1", 10), null);
		final VertexEntity<TestComplexEntity01> v2 = driver.graphCreateVertex(this.graphName, "to1-1",
			new TestComplexEntity01("v2-user", "desc2", 12), null);
		driver.graphCreateVertex(this.graphName, "from1-1", new TestComplexEntity01("v3-user", "desc3", 14), null);
		driver.graphCreateVertex(this.graphName, "to1-1", new TestComplexEntity01("v4-user", "desc4", 20), null);

		final EdgeEntity<?> edge = driver.graphCreateEdge(this.graphName, this.edgeCollectionName, null,
			v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity02(1, 2, 3), null);
		assertThat(edge.getCode(), is(202));

		final String rev = edge.getDocumentRevision();
		EdgeEntity<TestComplexEntity01> replacedEdge = driver.graphReplaceEdge(this.graphName, this.edgeCollectionName,
			edge.getDocumentKey(), v1.getDocumentHandle(), v2.getDocumentHandle(),
			new TestComplexEntity01("xx", "yy", 20), null, rev, null);
		assertThat(replacedEdge.getCode(), is(202));
		assertThat(replacedEdge.isError(), is(false));
		assertThat(replacedEdge.getDocumentKey(), is(edge.getDocumentKey()));
		assertThat(replacedEdge.getDocumentRevision(), is(not(edge.getDocumentRevision())));
		replacedEdge = driver.graphGetEdge(this.graphName, this.edgeCollectionName, edge.getDocumentKey(),
			TestComplexEntity01.class);
		assertThat(replacedEdge.getDocumentHandle(), is(edge.getDocumentHandle()));
		assertThat(replacedEdge.getFromVertexHandle(), is(v1.getDocumentHandle()));
		assertThat(replacedEdge.getToVertexHandle(), is(v2.getDocumentHandle()));

		assertThat(replacedEdge.getEntity(), instanceOf(TestComplexEntity01.class));
		assertThat(replacedEdge.getEntity().getUser(), is("xx"));
		assertThat(replacedEdge.getEntity().getDesc(), is("yy"));
		assertThat(replacedEdge.getEntity().getAge(), is(20));

	}

	@Test
	public void test_replace_edge_match_ne() throws ArangoException {

		driver.createGraph(this.graphName, this.createEdgeDefinitions(2, 0), this.createOrphanCollections(2), true);
		final VertexEntity<TestComplexEntity01> v1 = driver.graphCreateVertex(this.graphName, "from1-1",
			new TestComplexEntity01("v1-user", "desc1", 10), null);
		final VertexEntity<TestComplexEntity01> v2 = driver.graphCreateVertex(this.graphName, "to1-1",
			new TestComplexEntity01("v2-user", "desc2", 12), null);
		driver.graphCreateVertex(this.graphName, "from1-1", new TestComplexEntity01("v3-user", "desc3", 14), null);
		driver.graphCreateVertex(this.graphName, "to1-1", new TestComplexEntity01("v4-user", "desc4", 20), null);

		final EdgeEntity<?> edge = driver.graphCreateEdge(this.graphName, this.edgeCollectionName, null,
			v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity02(1, 2, 3), null);
		assertThat(edge.getCode(), is(202));

		try {
			final String rev = edge.getDocumentRevision() + 1;
			driver.graphReplaceEdge(this.graphName, this.edgeCollectionName, edge.getDocumentKey(),
				v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity01("xx", "yy", 20), null, rev,
				null);
			fail();
		} catch (final ArangoException e) {
			assertThat(e.getCode(), is(412));
			assertThat(e.getErrorNumber(), is(1903));
			assertThat(e.getErrorMessage(), is("wrong revision"));
		}

	}

	@Test
	public void test_replace_from_to() throws ArangoException {

		driver.createGraph(this.graphName, this.createEdgeDefinitions(2, 0), this.createOrphanCollections(2), true);
		final VertexEntity<TestComplexEntity01> v1 = driver.graphCreateVertex(this.graphName, "from1-1",
			new TestComplexEntity01("v1-user", "desc1", 10), null);
		final VertexEntity<TestComplexEntity01> v2 = driver.graphCreateVertex(this.graphName, "to1-1",
			new TestComplexEntity01("v2-user", "desc2", 12), null);
		driver.graphCreateVertex(this.graphName, "from1-1", new TestComplexEntity01("v3-user", "desc3", 14), null);
		driver.graphCreateVertex(this.graphName, "to1-1", new TestComplexEntity01("v4-user", "desc4", 20), null);

		final EdgeEntity<TestComplexEntity02> edge = driver.graphCreateEdge(this.graphName, this.edgeCollectionName,
			null, v1.getDocumentHandle(), v2.getDocumentHandle(), new TestComplexEntity02(1, 2, 3), null);
		assertThat(edge.getCode(), is(202));

		final String rev = edge.getDocumentRevision();
		EdgeEntity<TestComplexEntity02> replacedEdge = driver.graphReplaceEdge(this.graphName, this.edgeCollectionName,
			edge.getDocumentKey(), v2.getDocumentHandle(), v1.getDocumentHandle(), edge.getEntity(), null, rev, null);

		assertThat(replacedEdge.getCode(), is(202));
		assertThat(replacedEdge.isError(), is(false));
		assertThat(replacedEdge.getDocumentKey(), is(edge.getDocumentKey()));
		assertThat(replacedEdge.getDocumentRevision(), is(not(edge.getDocumentRevision())));
		assertThat(replacedEdge.getFromVertexHandle(), is(v2.getDocumentHandle()));
		assertThat(replacedEdge.getToVertexHandle(), is(v1.getDocumentHandle()));

		replacedEdge = driver.graphGetEdge(this.graphName, this.edgeCollectionName, edge.getDocumentKey(),
			TestComplexEntity02.class);

		assertThat(replacedEdge.getDocumentHandle(), is(edge.getDocumentHandle()));
		assertThat(replacedEdge.getFromVertexHandle(), is(v2.getDocumentHandle()));
		assertThat(replacedEdge.getToVertexHandle(), is(v1.getDocumentHandle()));
		assertThat(replacedEdge.getEntity(), instanceOf(TestComplexEntity02.class));
		assertThat(replacedEdge.getEntity().getX(), is(1));
		assertThat(replacedEdge.getEntity().getY(), is(2));
		assertThat(replacedEdge.getEntity().getZ(), is(3));

	}

}
