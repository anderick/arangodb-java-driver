package com.arangodb.velocypack;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.arangodb.velocypack.exception.VPackBuilderUnexpectedValueException;
import com.arangodb.velocypack.exception.VPackException;
import com.arangodb.velocypack.exception.VPackNumberOutOfRangeException;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class BuilderTest {

	@Test
	public void addNull() throws VPackException {
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Null));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isNull());
	}

	@Test
	public void addBooleanTrue() throws VPackException {
		final Builder builder = new Builder();
		builder.add(new Value(true));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isBoolean());
		Assert.assertTrue(slice.getBoolean());
	}

	@Test
	public void addBooleanFalse() throws VPackException {
		final Builder builder = new Builder();
		builder.add(new Value(false));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isBoolean());
		Assert.assertFalse(slice.getBoolean());
	}

	@Test
	public void addBooleanNull() throws VPackException {
		final Builder builder = new Builder();
		final Boolean b = null;
		builder.add(new Value(b));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isNull());
	}

	@Test
	public void addDouble() throws VPackException {
		final Builder builder = new Builder();
		final double value = Double.MAX_VALUE;
		builder.add(new Value(value));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isDouble());
		Assert.assertEquals(value, slice.getDouble(), 0);
	}

	@Test
	public void addIntegerAsSmallIntMin() throws VPackException {
		final Builder builder = new Builder();
		final int value = -6;
		builder.add(new Value(value, ValueType.SmallInt));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getSmallInt());
	}

	@Test
	public void addIntegerAsSmallIntMax() throws VPackException {
		final Builder builder = new Builder();
		final int value = 9;
		builder.add(new Value(value, ValueType.SmallInt));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getSmallInt());
	}

	@Test(expected = VPackNumberOutOfRangeException.class)
	public void addIntegerAsSmallIntOutofRange() throws VPackException {
		final Builder builder = new Builder();
		final int value = Integer.MAX_VALUE;
		builder.add(new Value(value, ValueType.SmallInt));
	}

	@Test
	public void addLongAsSmallIntMin() throws VPackException {
		final Builder builder = new Builder();
		final long value = -6;
		builder.add(new Value(value, ValueType.SmallInt));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getSmallInt());
	}

	@Test
	public void addLongAsSmallIntMax() throws VPackException {
		final Builder builder = new Builder();
		final long value = 9;
		builder.add(new Value(value, ValueType.SmallInt));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getSmallInt());
	}

	@Test(expected = VPackNumberOutOfRangeException.class)
	public void addLongAsSmallIntOutofRange() throws VPackException {
		final Builder builder = new Builder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.SmallInt));
	}

	@Test
	public void addBigIntegerAsSmallIntMin() throws VPackException {
		final Builder builder = new Builder();
		final BigInteger value = BigInteger.valueOf(-6);
		builder.add(new Value(value, ValueType.SmallInt));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, BigInteger.valueOf(slice.getSmallInt()));
	}

	@Test
	public void addBigIntegerAsSmallIntMax() throws VPackException {
		final Builder builder = new Builder();
		final BigInteger value = BigInteger.valueOf(9);
		builder.add(new Value(value, ValueType.SmallInt));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, BigInteger.valueOf(slice.getSmallInt()));
	}

	@Test(expected = VPackNumberOutOfRangeException.class)
	public void addBigIntegerAsSmallIntOutofRange() throws VPackException {
		final Builder builder = new Builder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.SmallInt));
	}

	@Test
	public void addIntegerAsInt() throws VPackException {
		final Builder builder = new Builder();
		final int value = Integer.MAX_VALUE;
		builder.add(new Value(value, ValueType.Int));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isInt());
		Assert.assertEquals(value, slice.getInt());
	}

	@Test
	public void addLongAsInt() throws VPackException {
		final Builder builder = new Builder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.Int));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isInt());
		Assert.assertEquals(value, slice.getInt());
	}

	@Test
	public void addBigIntegerAsInt() throws VPackException {
		final Builder builder = new Builder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.Int));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isInt());
		Assert.assertEquals(value, BigInteger.valueOf(slice.getInt()));
	}

	@Test
	public void addLongAsUInt() throws VPackException {
		final Builder builder = new Builder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.UInt));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isUInt());
		Assert.assertEquals(value, slice.getUInt());
	}

	@Test
	public void addBigIntegerAsUInt() throws VPackException {
		final Builder builder = new Builder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.UInt));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isUInt());
		Assert.assertEquals(value, slice.getUIntAsBigInteger());
	}

	@Test(expected = VPackBuilderUnexpectedValueException.class)
	public void addLongAsUIntNegative() throws VPackException {
		final Builder builder = new Builder();
		final long value = -1;
		builder.add(new Value(value, ValueType.UInt));
	}

	@Test(expected = VPackBuilderUnexpectedValueException.class)
	public void addBigIntegerAsUIntNegative() throws VPackException {
		final Builder builder = new Builder();
		final BigInteger value = BigInteger.valueOf(-1);
		builder.add(new Value(value, ValueType.UInt));
	}

	@Test
	public void addUTCDate() throws VPackException {
		final Builder builder = new Builder();
		final Date date = new Date();
		builder.add(new Value(date));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isUTCDate());
		Assert.assertEquals(date, slice.getUTCDate());
	}

	@Test
	public void addStringShort() throws VPackException {
		final Builder builder = new Builder();
		final String s = "Hallo Welt!";
		builder.add(new Value(s));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isString());
		Assert.assertEquals(s, slice.getString());
	}

	@Test
	public void addStringLong() throws VPackException {
		final Builder builder = new Builder();
		final String s = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus.";
		builder.add(new Value(s));

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isString());
		Assert.assertEquals(s, slice.getString());
	}

	@Test
	public void emptyArray() throws VPackException {
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Array));
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(0, slice.getLength());
		Assert.assertEquals(1, slice.getVpack().length);
	}

	@Test
	public void emptyObject() throws VPackException {
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Object));
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(0, slice.getLength());
		Assert.assertEquals(1, slice.getVpack().length);
	}

}
