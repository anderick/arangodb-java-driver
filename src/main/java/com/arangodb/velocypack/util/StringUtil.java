package com.arangodb.velocypack.util;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class StringUtil {

	public static String toString(final byte[] array, final int offset, final int length) {
		final byte[] value = new byte[length];
		System.arraycopy(array, offset, value, 0, length);
		return new String(value);
	}

	public static byte[] toByteArray(final String value) {
		return value.getBytes();
	}

}
