package com.arangodb.velocypack.util;

import java.io.UnsupportedEncodingException;
import java.util.Collection;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class StringUtil {

	private StringUtil() {
		super();
	}

	public static String toString(final byte[] array, final int offset, final int length) {
		return new String(array, offset, length);
	}

	public static void append(final Collection<Byte> buffer, final String value) throws UnsupportedEncodingException {
		final byte[] bytes = value.getBytes("UTF-8");
		for (final byte b : bytes) {
			buffer.add(b);
		}
	}

}
