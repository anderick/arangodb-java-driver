package com.arangodb.velocypack.util;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class StringUtil {

	public static String toString(final byte[] array, final int offset, final int length) {
		return new String(array, offset, length);
	}

	public static byte[] toByteArray(final String value) {
		return value.getBytes();
	}

}
