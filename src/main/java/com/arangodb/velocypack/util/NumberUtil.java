package com.arangodb.velocypack.util;

import java.math.BigInteger;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class NumberUtil {

	public static double toDouble(final byte[] array, final int offset, final int length) {
		return Double.longBitsToDouble(toLong(array, offset, Double.BYTES));
	}

	public static byte[] toByteArray(final double value) {
		return toByteArray(Double.doubleToRawLongBits(value), Double.BYTES);
	}

	public static long toLong(final byte[] array, final int offset, final int length) {
		long result = 0;
		for (int i = offset; i < (offset + length); i++) {
			result <<= 8;
			result |= (array[i] & 0xFF);
		}
		return result;
	}

	public static byte[] toByteArray(final long value, final int length) {
		long l = value;
		final byte[] result = new byte[length];
		for (int i = (length - 1); i >= 0; i--) {
			result[i] = (byte) (l & 0xFF);
			l >>= 8;
		}
		return result;
	}

	public static BigInteger toBigInteger(final byte[] array, final int offset, final int length) {
		final byte[] value = new byte[length];
		// TODO dont copy
		System.arraycopy(array, offset, value, 0, length);
		return new BigInteger(1, value);
	}

	public static byte[] toByteArray(final BigInteger value, final int length) {
		final byte[] array = value.toByteArray();
		final byte[] result = new byte[length];
		// TODO dont copy
		System.arraycopy(array, array.length - length, result, 0, length);
		return result;
	}
}
