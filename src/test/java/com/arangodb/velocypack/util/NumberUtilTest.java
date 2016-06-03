package com.arangodb.velocypack.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class NumberUtilTest {

	@Test
	public void convertDouble() {
		final double value = Double.MAX_VALUE;
		final byte[] byteArray = NumberUtil.toByteArray(value);
		final double value2 = NumberUtil.toDouble(byteArray, 0, byteArray.length);
		Assert.assertEquals(value, value2, 0);
	}

	@Test
	public void convertLong() {
		final long value = Long.MAX_VALUE;
		final byte[] byteArray = NumberUtil.toByteArray(value, 8);
		final long value2 = NumberUtil.toLong(byteArray, 0, byteArray.length);
		Assert.assertEquals(value, value2, 0);
	}

	@Test
	public void appendLong() {
		final long value = Long.MAX_VALUE;
		final Collection<Byte> buffer = new ArrayList<Byte>();
		NumberUtil.append(buffer, value, 8);
		final long value2 = NumberUtil.toLong(toArray(buffer), 0, 8);
		Assert.assertEquals(value, value2);
	}

	@Test
	public void appendLongReversed() {
		final long value = Long.MAX_VALUE;
		final Collection<Byte> buffer = new ArrayList<Byte>();
		NumberUtil.appendReversed(buffer, value, 8);
		final long value2 = NumberUtil.toLongReversed(toArray(buffer), 0, 8);
		Assert.assertEquals(value, value2);
	}

	@Test
	public void convertBigInteger() {
		final BigInteger maxLong = BigInteger.valueOf(Long.MAX_VALUE);
		final BigInteger value = maxLong.add(maxLong);
		final byte[] byteArray = NumberUtil.toByteArray(value, 8);
		final BigInteger value2 = NumberUtil.toBigInteger(byteArray, 0, byteArray.length);
		Assert.assertEquals(value, value2);
	}

	@Test
	public void appendBigInteger() {
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		final Collection<Byte> buffer = new ArrayList<Byte>();
		NumberUtil.append(buffer, value, 8);
		final BigInteger value2 = NumberUtil.toBigInteger(toArray(buffer), 0, 8);
		Assert.assertEquals(value, value2);
	}

	private byte[] toArray(final Collection<Byte> buffer) {
		final byte[] array = new byte[buffer.size()];
		int i = 0;
		for (final byte b : buffer) {
			array[i++] = b;
		}
		return array;
	}

}
