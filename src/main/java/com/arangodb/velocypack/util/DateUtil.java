package com.arangodb.velocypack.util;

import java.util.Collection;
import java.util.Date;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class DateUtil {

	private DateUtil() {
		super();
	}

	public static void append(final Collection<Byte> buffer, final Date value) {
		NumberUtil.append(buffer, value.getTime(), 8);
	}

	public static Date toDate(final byte[] array, final int offset, final int length) {
		final long milliseconds = NumberUtil.toLong(array, offset, length);
		return new Date(milliseconds);
	}
}
