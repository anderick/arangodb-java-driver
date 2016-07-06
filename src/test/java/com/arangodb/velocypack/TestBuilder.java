package com.arangodb.velocypack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.arangodb.velocypack.exception.VPackBuilderException;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class TestBuilder {

	public static void recurse(final VPackBuilder b, final int curdepth, final int depth, final int size)
			throws VPackBuilderException {
		if (curdepth >= depth) {
			return;
		}
		for (int i = 0; i < size; i++) {
			b.add("Hallo" + i, new Value(i));
		}
		for (int i = 0; i < size; i++) {
			b.add("String" + i, new Value("TextTextText" + i));
		}
		b.add("sub1", new Value(ValueType.OBJECT));
		recurse(b, curdepth + 1, depth, size);
		b.close();

		b.add("sub2", new Value(ValueType.OBJECT));
		recurse(b, curdepth + 1, depth, size);
		b.close();

		b.add("arr1", new Value(ValueType.ARRAY));
		for (int i = 0; i < size; i++) {
			b.add(new Value(i));
		}
		b.close();
	}

	public static void main(final String[] args) throws VPackBuilderException, IOException {
		if (args.length < 3) {
			System.out.println("Usage: DEPTH SIZE TARGETFILE");
			return;
		}
		final int depth = Integer.valueOf(args[0]);
		final int size = Integer.valueOf(args[1]);
		final File file = new File(args[2]);

		final long start = System.currentTimeMillis();

		final VPackBuilder b = new VPackBuilder();
		b.add(new Value(ValueType.OBJECT));
		recurse(b, 0, depth, size);
		b.close();

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
