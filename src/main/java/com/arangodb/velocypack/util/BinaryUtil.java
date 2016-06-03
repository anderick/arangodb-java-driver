package com.arangodb.velocypack.util;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class BinaryUtil {

	public static byte[] toBinary(final byte[] array, final int offset, final int length) {
		final byte[] result = new byte[length];
		for (int i = offset, j = 0; j < length; i++, j++) {
			result[j] = array[i];
		}
		return result;
	}

}