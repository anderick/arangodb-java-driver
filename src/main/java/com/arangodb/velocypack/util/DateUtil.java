package com.arangodb.velocypack.util;

import java.util.Date;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class DateUtil {

	public static byte[] toByteArray(final Date value) {
		return NumberUtil.toByteArray(value.getTime(), 8);
	}

	public static Date toDate(final byte[] array, final int offset, final int length) {
		final long milliseconds = NumberUtil.toLong(array, offset, length);
		return new Date(milliseconds);
	}
}
