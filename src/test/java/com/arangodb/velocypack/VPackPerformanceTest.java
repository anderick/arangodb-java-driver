package com.arangodb.velocypack;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.arangodb.velocypack.exception.VPackParserException;
import com.google.gson.Gson;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackPerformanceTest {

	private static final int DOCUMENTS = 100000;

	protected static class PerfTestEntity {

		public static final String REV = "_rev";
		public static final String KEY = "_key";
		public static final String ID = "_id";

		@com.google.gson.annotations.SerializedName(REV)
		@com.arangodb.velocypack.annotations.SerializedName(REV)
		private final long documentRevision;

		@com.google.gson.annotations.SerializedName(ID)
		@com.arangodb.velocypack.annotations.SerializedName(ID)
		private final String documentHandle;

		@com.google.gson.annotations.SerializedName(KEY)
		@com.arangodb.velocypack.annotations.SerializedName(KEY)
		private final String documentKey;

		@SuppressWarnings("unused")
		private final Map<String, Object> parameters;

		public PerfTestEntity() {
			super();
			documentHandle = "documentHandle";
			documentKey = "documentKey";
			documentRevision = 1234567890L;
			parameters = new HashMap<String, Object>();
		}

	}

	@Test
	public void serializeVpack() throws VPackParserException {
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < DOCUMENTS; i++) {
			vpack.serialize(new PerfTestEntity());
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize Vpack: " + time + "ms");
	}

	@Test
	public void deserializeVpack() throws VPackParserException {
		final VPackSlice serializedVpack = new VPack().serialize(new PerfTestEntity());
		final VPack vpack = new VPack();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < DOCUMENTS; i++) {
			vpack.deserialize(serializedVpack, PerfTestEntity.class);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize Vpack: " + time + "ms");
	}

	@Test
	public void serializeJson() {
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < DOCUMENTS; i++) {
			gson.toJson(new PerfTestEntity());
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("serialize Json: " + time + "ms");
	}

	@Test
	public void deserializeJson() {
		final String serializedJson = new Gson().toJson(new PerfTestEntity());
		final Gson gson = new Gson();

		final long start = System.currentTimeMillis();
		for (int i = 0; i < DOCUMENTS; i++) {
			gson.fromJson(serializedJson, PerfTestEntity.class);
		}
		final long time = System.currentTimeMillis() - start;
		System.out.println("deserialize Json: " + time + "ms");
	}

}
