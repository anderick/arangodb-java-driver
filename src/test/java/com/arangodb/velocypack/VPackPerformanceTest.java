package com.arangodb.velocypack;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.arangodb.velocypack.exception.VPackParserException;
import com.google.gson.Gson;

/**
 * @author Mark - mark@arangodb.com
 *
 */
@SuppressWarnings("unused")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VPackPerformanceTest {

	private static final String BIG_JSON = "/big.json";
	private static final String SMALL_JSON = "/small.json";
	private static final int SMALL_DOCUMENTS = 10000;
	private static final int BIG_DOCUMENTS = 10;

	protected static class PerfTestEntity {

		public static final String REV = "_rev";
		public static final String KEY = "_key";
		public static final String ID = "_id";

		@com.google.gson.annotations.SerializedName(REV)
		@com.arangodb.velocypack.annotations.SerializedName(REV)
		private long documentRevision;

		@com.google.gson.annotations.SerializedName(ID)
		@com.arangodb.velocypack.annotations.SerializedName(ID)
		private String documentHandle;

		@com.google.gson.annotations.SerializedName(KEY)
		@com.arangodb.velocypack.annotations.SerializedName(KEY)
		private String documentKey;

		private final Map<String, Object> parameters;

		public PerfTestEntity() {
			super();
			parameters = new HashMap<String, Object>();
		}

		public PerfTestEntity(final long documentRevision, final String documentHandle, final String documentKey) {
			this();
			this.documentRevision = documentRevision;
			this.documentHandle = documentHandle;
			this.documentKey = documentKey;
		}

	}

	protected static class Features {
		private String type;
		private Feature[] features;
	}

	protected static class Feature {
		private String type;
		private Properties properties;
		private Geometry geometry;
	}

	protected static class Properties {
		private String MAPBLKLOT;
		private String BLKLOT;
		private String BLOCK_NUM;
		private String LOT_NUM;
		private String FROM_ST;
		private String TO_ST;
		private String STREET;
		private String ST_TYPE;
		private String ODD_EVEN;
	}

	protected static class Geometry {
		private String type;
		private double[][][] coordinates;
	}

	@Test
	public void test02_serializeVpack() throws VPackParserException {
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < SMALL_DOCUMENTS; i++) {
			vpack.serialize(new PerfTestEntity());
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize base VPack: " + time + "ms");
	}

	@Test
	public void test04_deserializeVpack() throws VPackParserException {
		final VPackSlice serializedVpack = new VPack()
				.serialize(new PerfTestEntity(1234567890L, "documentHandle", "documentKey"));
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < SMALL_DOCUMENTS; i++) {
			final PerfTestEntity deserialize = vpack.deserialize(serializedVpack, PerfTestEntity.class);
			Assert.assertNotNull(deserialize.documentHandle);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize base VPack: " + time + "ms");
	}

	@Test
	public void test06_serializeSmallVpack() throws VPackParserException {
		final InputStream in = getClass().getResourceAsStream(SMALL_JSON);
		final InputStreamReader reader = new InputStreamReader(in);
		final Features entity = new Gson().fromJson(reader, Features.class);
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < SMALL_DOCUMENTS; i++) {
			vpack.serialize(entity);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize small VPack: " + time + "ms");
	}

	@Test
	public void test08_deserializeSmallVpack() throws VPackParserException, IOException {
		final InputStream in = getClass().getResourceAsStream(SMALL_JSON);
		final InputStreamReader reader = new InputStreamReader(in);
		final Features entity = new Gson().fromJson(reader, Features.class);
		Assert.assertNotNull(entity.type);
		final VPackSlice serializedVpack = new VPack().serialize(entity);
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < SMALL_DOCUMENTS; i++) {
			final Features deserialize = vpack.deserialize(serializedVpack, Features.class);
			Assert.assertNotNull(deserialize.type);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize small VPack: " + time + "ms");
	}

	@Test
	public void test10_serializeBigVpack() throws VPackParserException {
		final InputStream in = getClass().getResourceAsStream(BIG_JSON);
		final InputStreamReader reader = new InputStreamReader(in);
		final Features entity = new Gson().fromJson(reader, Features.class);
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < BIG_DOCUMENTS; i++) {
			vpack.serialize(entity);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize big VPack: " + time + "ms");
	}

	@Test
	public void test12_deserializeBigVpack() throws VPackParserException {
		final InputStream in = getClass().getResourceAsStream(BIG_JSON);
		final InputStreamReader reader = new InputStreamReader(in);
		final Features entity = new Gson().fromJson(reader, Features.class);
		final VPackSlice serializedVpack = new VPack().serialize(entity);
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < BIG_DOCUMENTS; i++) {
			final Features deserialize = vpack.deserialize(serializedVpack, Features.class);
			Assert.assertNotNull(deserialize.type);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize big VPack: " + time + "ms");
	}

	@Test
	public void test01_serializeJson() {
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < SMALL_DOCUMENTS; i++) {
			gson.toJson(new PerfTestEntity());
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize base Json: " + time + "ms");
	}

	@Test
	public void test03_deserializeJson() {
		final String serializedJson = new Gson()
				.toJson(new PerfTestEntity(1234567890L, "documentHandle", "documentKey"));
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < SMALL_DOCUMENTS; i++) {
			final PerfTestEntity fromJson = gson.fromJson(serializedJson, PerfTestEntity.class);
			Assert.assertNotNull(fromJson.documentHandle);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize base Json: " + time + "ms");
	}

	@Test
	public void test05_serializeSmallJson() {
		final InputStream in = getClass().getResourceAsStream(SMALL_JSON);
		final InputStreamReader reader = new InputStreamReader(in);
		final Features entity = new Gson().fromJson(reader, Features.class);
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < SMALL_DOCUMENTS; i++) {
			gson.toJson(entity);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize small Json: " + time + "ms");
	}

	@Test
	public void test07_deserializeSmallJson() throws IOException {
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < SMALL_DOCUMENTS; i++) {
			final InputStream in = getClass().getResourceAsStream(SMALL_JSON);
			final InputStreamReader reader = new InputStreamReader(in);
			final Features fromJson = gson.fromJson(reader, Features.class);
			reader.close();
			Assert.assertNotNull(fromJson.type);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize small Json: " + time + "ms");
	}

	@Test
	public void test09_serializeBigJson() {
		final InputStream in = getClass().getResourceAsStream(BIG_JSON);
		final InputStreamReader reader = new InputStreamReader(in);
		final Features entity = new Gson().fromJson(reader, Features.class);
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < BIG_DOCUMENTS; i++) {
			gson.toJson(entity);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize big Json: " + time + "ms");
	}

	@Test
	public void test11_deserializeBigJson() throws IOException {
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < BIG_DOCUMENTS; i++) {
			final InputStream in = getClass().getResourceAsStream(BIG_JSON);
			final InputStreamReader reader = new InputStreamReader(in);
			final Features fromJson = gson.fromJson(reader, Features.class);
			reader.close();
			Assert.assertNotNull(fromJson.type);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize big Json: " + time + "ms");
	}

}
