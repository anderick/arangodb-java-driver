package com.arangodb.velocypack;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.arangodb.velocypack.TestBuilder.TestEntity;
import com.arangodb.velocypack.exception.VPackException;
import com.google.gson.Gson;

/**
 * @author Mark - mark@arangodb.com
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VPackPerformanceTest {

	private static final String BIG_DOCUMENTS_DESCRIPTION = "100mb";
	private static final int BIG_DOCUMENTS_ITERATIONS = 10;
	private static final int BIG_DOCUMENTS_DEPTH = 18;
	private static final int BIG_DOCUMENTS_SIZE = 50;

	private static final String SMALL_DOCUMENTS_DESCRIPTION = "1mb";
	private static final int SMALL_DOCUMENTS_ITERATIONS = BIG_DOCUMENTS_ITERATIONS * 100;
	private static final int SMALL_DOCUMENTS_DEPTH = 11;
	private static final int SMALL_DOCUMENTS_SIZE = 70;

	private static final String VERY_SMALL_DOCUMENTS_DESCRIPTION = "1kb";
	private static final int VERY_SMALL_DOCUMENTS_ITERATIONS = SMALL_DOCUMENTS_ITERATIONS * 1000;
	private static final int VERY_SMALL_DOCUMENTS_DEPTH = 2;
	private static final int VERY_SMALL_DOCUMENTS_SIZE = 5;

	@Test
	public void test02_serializeVerySmallVpack() throws VPackException {
		serializeVpack(VERY_SMALL_DOCUMENTS_DESCRIPTION, VERY_SMALL_DOCUMENTS_ITERATIONS, VERY_SMALL_DOCUMENTS_DEPTH,
			VERY_SMALL_DOCUMENTS_SIZE);
	}

	@Test
	public void test04_deserializeVerySmallVpack() throws VPackException {
		deserializeVpack(VERY_SMALL_DOCUMENTS_DESCRIPTION, VERY_SMALL_DOCUMENTS_ITERATIONS, VERY_SMALL_DOCUMENTS_DEPTH,
			VERY_SMALL_DOCUMENTS_SIZE);
	}

	@Test
	public void test06_serializeSmallVpack() throws VPackException {
		serializeVpack(SMALL_DOCUMENTS_DESCRIPTION, SMALL_DOCUMENTS_ITERATIONS, SMALL_DOCUMENTS_DEPTH,
			SMALL_DOCUMENTS_SIZE);
	}

	@Test
	public void test08_deserializeSmallVpack() throws VPackException {
		deserializeVpack(SMALL_DOCUMENTS_DESCRIPTION, SMALL_DOCUMENTS_ITERATIONS, SMALL_DOCUMENTS_DEPTH,
			SMALL_DOCUMENTS_SIZE);
	}

	@Test
	public void test10_serializeBigVpack() throws VPackException {
		serializeVpack(BIG_DOCUMENTS_DESCRIPTION, BIG_DOCUMENTS_ITERATIONS, BIG_DOCUMENTS_DEPTH, BIG_DOCUMENTS_SIZE);
	}

	@Test
	public void test12_deserializeBigVpack() throws VPackException {
		deserializeVpack(BIG_DOCUMENTS_DESCRIPTION, BIG_DOCUMENTS_ITERATIONS, BIG_DOCUMENTS_DEPTH, BIG_DOCUMENTS_SIZE);
	}

	@Test
	public void test01_serializeVerySmallJson() {
		serializeJson(VERY_SMALL_DOCUMENTS_DESCRIPTION, VERY_SMALL_DOCUMENTS_ITERATIONS, VERY_SMALL_DOCUMENTS_DEPTH,
			VERY_SMALL_DOCUMENTS_SIZE);
	}

	@Test
	public void test03_deserializeVerySmallJson() {
		deserializeJson(VERY_SMALL_DOCUMENTS_DESCRIPTION, VERY_SMALL_DOCUMENTS_ITERATIONS, VERY_SMALL_DOCUMENTS_DEPTH,
			VERY_SMALL_DOCUMENTS_SIZE);
	}

	@Test
	public void test05_serializeSmallJson() {
		serializeJson(SMALL_DOCUMENTS_DESCRIPTION, SMALL_DOCUMENTS_ITERATIONS, SMALL_DOCUMENTS_DEPTH,
			SMALL_DOCUMENTS_SIZE);
	}

	@Test
	public void test07_deserializeSmallJson() throws IOException {
		deserializeJson(SMALL_DOCUMENTS_DESCRIPTION, SMALL_DOCUMENTS_ITERATIONS, SMALL_DOCUMENTS_DEPTH,
			SMALL_DOCUMENTS_SIZE);
	}

	@Test
	public void test09_serializeBigJson() {
		serializeJson(BIG_DOCUMENTS_DESCRIPTION, BIG_DOCUMENTS_ITERATIONS, BIG_DOCUMENTS_DEPTH, BIG_DOCUMENTS_SIZE);
	}

	@Test
	public void test11_deserializeBigJson() throws IOException {
		deserializeJson(BIG_DOCUMENTS_DESCRIPTION, BIG_DOCUMENTS_ITERATIONS, BIG_DOCUMENTS_DEPTH, BIG_DOCUMENTS_SIZE);
	}

	private static void serializeVpack(final String description, final int iterations, final int depth, final int size)
			throws VPackException {
		final VPack vpack = new VPack();
		final TestEntity entity = TestBuilder.buildEntity(depth, size);

		final long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			vpack.serialize(entity);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize " + iterations + " x " + description + " VPack: " + time + "ms");
	}

	private static void deserializeVpack(
		final String description,
		final int iterations,
		final int depth,
		final int size) throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		TestBuilder.buildVpack(builder, depth, size);
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			vpack.deserialize(builder.slice(), TestEntity.class);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize " + iterations + " x " + description + " VPack: " + time + "ms");
	}

	private static void serializeJson(final String description, final int iterations, final int depth, final int size) {
		final TestEntity entity = TestBuilder.buildEntity(depth, size);
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			gson.toJson(entity);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize " + iterations + " x " + description + " Json: " + time + "ms");
	}

	private static void deserializeJson(
		final String description,
		final int iterations,
		final int depth,
		final int size) {
		final String serializedJson = TestBuilder.buildJson(depth, size);
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < iterations; i++) {
			gson.fromJson(serializedJson, TestEntity.class);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize " + iterations + " x " + description + " Json: " + time + "ms");
	}
}
