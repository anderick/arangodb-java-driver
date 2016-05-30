package com.arangodb.velocypack.util;

import java.math.BigInteger;

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
	public void convertBigInteger() {
		final BigInteger maxLong = new BigInteger(String.valueOf(Long.MAX_VALUE));
		final BigInteger value = maxLong.add(maxLong);
		final byte[] byteArray = NumberUtil.toByteArray(value, 8);
		final BigInteger value2 = NumberUtil.toBigInteger(byteArray, 0, byteArray.length);
		Assert.assertEquals(value, value2);
	}

}
