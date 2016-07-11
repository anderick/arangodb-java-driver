package com.arangodb.velocypack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.arangodb.velocypack.exception.VPackException;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class TestBuilder {

	@SuppressWarnings("unused")
	protected static class TestEntity {
		private String attr1;
		private int attr2;
		private long attr3;
		private double attr4;
		private boolean attr5;
		private TestEntity sub1;
		private TestEntity sub2;
		private int[] arr1;
	}

	private static TestEntity recurse(final int curdeph, final int depth, final int size) {
		if (curdeph >= depth) {
			return null;
		}
		final TestEntity entity = new TestEntity();
		entity.attr1 = "TextTextText" + depth;
		entity.attr2 = depth;
		entity.attr3 = depth;
		entity.attr4 = depth + 0.5;
		entity.attr5 = true;
		entity.arr1 = new int[size];
		for (int i = 0; i < size; i++) {
			entity.arr1[i] = i;
		}
		entity.sub1 = recurse(curdeph + 1, depth, size);
		entity.sub2 = recurse(curdeph + 1, depth, size);
		return entity;
	}

	public static TestEntity buildEntity(final int depth, final int size) {
		return recurse(0, depth, size);
	}

	private static void recurse(final VPackBuilder builder, final int curdepth, final int depth, final int size)
			throws VPackException {
		if (curdepth >= depth) {
			return;
		}
		builder.add("attr1", new Value("TextTextText" + depth));
		builder.add("attr2", new Value(depth));
		builder.add("attr3", new Value(depth));
		builder.add("attr4", new Value(depth + 0.5));
		builder.add("attr5", new Value(true));
		// for (int i = 0; i < size; i++) {
		// builder.add("Hallo" + i, new Value(i));
		// }
		// for (int i = 0; i < size; i++) {
		// builder.add("String" + i, new Value("TextTextText" + i));
		// }
		builder.add("sub1", new Value(ValueType.OBJECT));
		recurse(builder, curdepth + 1, depth, size);
		builder.close();

		builder.add("sub2", new Value(ValueType.OBJECT));
		recurse(builder, curdepth + 1, depth, size);
		builder.close();

		builder.add("arr1", new Value(ValueType.ARRAY));
		for (int i = 0; i < size; i++) {
			builder.add(new Value(i));
		}
		builder.close();
	}

	public static void buildVpack(final VPackBuilder builder, final int depth, final int size) throws VPackException {
		builder.add(new Value(ValueType.OBJECT));
		recurse(builder, 0, depth, size);
		builder.close();
	}

	private static void recurse(final JsonObject obj, final int curdepth, final int depth, final int size) {
		if (curdepth >= depth) {
			return;
		}
		obj.addProperty("attr1", "TextTextText" + depth);
		obj.addProperty("attr2", depth);
		obj.addProperty("attr3", depth);
		obj.addProperty("attr4", depth + 0.5);
		obj.addProperty("attr5", true);
		final JsonObject subObj1 = new JsonObject();
		recurse(subObj1, curdepth + 1, depth, size);
		obj.add("sub1", subObj1);

		final JsonObject subObj2 = new JsonObject();
		recurse(subObj2, curdepth + 1, depth, size);
		obj.add("sub2", subObj1);

		final JsonArray arr1 = new JsonArray();
		for (int i = 0; i < size; i++) {
			arr1.add(i);
		}
		obj.add("arr1", arr1);
	}

	public static String buildJson(final int depth, final int size) {
		final JsonObject obj = new JsonObject();
		recurse(obj, 0, depth, size);
		return new Gson().toJson(obj);
	}

	public static void main(final String[] args) throws VPackException, IOException {
		if (args.length < 3) {
			System.out.println("Usage: DEPTH SIZE TARGETFILE");
			return;
		}
		final int depth = Integer.valueOf(args[0]);
		final int size = Integer.valueOf(args[1]);
		final File file = new File(args[2]);

		final long start = System.currentTimeMillis();

		final VPackBuilder b = new VPackBuilder();
		buildVpack(b, depth, size);

		final long end = System.currentTimeMillis();
		System.out.println("Runtime: " + (end - start));
		System.out.println("Size of result (Slice): " + b.slice().getVpack().length);
		final byte[] out = Arrays.copyOfRange(b.slice().getVpack(), 0, b.getVpackSize());
		System.out.println("Size of result (VPack): " + out.length);

		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		final FileOutputStream os = new FileOutputStream(file);
		os.write(out);
		os.close();
	}

}
