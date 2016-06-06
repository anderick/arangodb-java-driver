package com.arangodb.velocypack;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.arangodb.velocypack.Builder.BuilderOptions;
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
	public void compactArray() throws VPackException {
		final long[] expected = { 1, 16 };
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Array, true));
		for (final long l : expected) {
			builder.add(new Value(l));
		}
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(2, slice.getLength());
		for (int i = 0; i < expected.length; i++) {
			final Slice at = slice.at(i);
			Assert.assertTrue(at.isInteger());
			Assert.assertEquals(expected[i], at.getInteger());
		}
	}

	@Test
	public void unindexedArray() throws VPackException {
		final long[] expected = { 1, 16 };
		final BuilderOptions options = new BuilderOptions();
		options.setBuildUnindexedArrays(true);
		final Builder builder = new Builder(options);
		builder.add(new Value(ValueType.Array, false));
		for (final long l : expected) {
			builder.add(new Value(l));
		}
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(2, slice.getLength());
		for (int i = 0; i < expected.length; i++) {
			final Slice at = slice.at(i);
			Assert.assertTrue(at.isInteger());
			Assert.assertEquals(expected[i], at.getInteger());
		}
	}

	@Test
	public void indexedArray() throws VPackException {
		final long[] values = { 1, 2, 3 };
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Array));
		for (final long l : values) {
			builder.add(new Value(l));
		}
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(3, slice.getLength());
	}

	@Test
	public void arrayInArray() throws VPackException {
		final long[][] values = { { 1, 2, 3 }, { 1, 2, 3 } };
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Array));
		for (final long[] ls : values) {
			builder.add(new Value(ValueType.Array));
			for (final long l : ls) {
				builder.add(new Value(l));
			}
			builder.close();
		}
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(values.length, slice.getLength());
		for (int i = 0; i < values.length; i++) {
			final Slice ls = slice.at(i);
			Assert.assertTrue(ls.isArray());
			Assert.assertEquals(values[i].length, ls.getLength());
			for (int j = 0; j < values[i].length; j++) {
				final Slice l = ls.at(j);
				Assert.assertTrue(l.isInteger());
				Assert.assertEquals(values[i][j], l.getInteger());
			}
		}
	}

	@Test
	public void arrayInArrayInArray() throws VPackException {
		final long[][][] values = { { { 1, 2, 3 } } };
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Array));
		for (final long[][] lss : values) {
			builder.add(new Value(ValueType.Array));
			for (final long[] ls : lss) {
				builder.add(new Value(ValueType.Array));
				for (final long l : ls) {
					builder.add(new Value(l));
				}
				builder.close();
			}
			builder.close();
		}
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(values.length, slice.getLength());
		for (int i = 0; i < values.length; i++) {
			final Slice lls = slice.at(i);
			Assert.assertTrue(lls.isArray());
			Assert.assertEquals(values[i].length, lls.getLength());
			for (int j = 0; j < values[i].length; j++) {
				final Slice ls = lls.at(i);
				Assert.assertTrue(ls.isArray());
				Assert.assertEquals(values[i][j].length, ls.getLength());
				for (int k = 0; k < values[i][j].length; k++) {
					final Slice l = ls.at(k);
					Assert.assertTrue(l.isInteger());
					Assert.assertEquals(values[i][j][k], l.getInteger());
				}
			}

		}
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

	@Test
	public void compactObject() throws VPackException {
		// {"a": 12, "b": true, "c": "xyz"}
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Object, true));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(3, slice.getLength());
		Assert.assertEquals(12, slice.get("a").getInteger());
		Assert.assertEquals(true, slice.get("b").getBoolean());
		Assert.assertEquals("xyz", slice.get("c").getString());
	}

	@Test
	public void unindexedObject() throws VPackException {
		// {"a": 12, "b": true, "c": "xyz"}
		final BuilderOptions options = new BuilderOptions();
		options.setBuildUnindexedObjects(true);
		final Builder builder = new Builder(options);
		builder.add(new Value(ValueType.Object, false));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(3, slice.getLength());
		Assert.assertEquals(12, slice.get("a").getInteger());
		Assert.assertEquals(true, slice.get("b").getBoolean());
		Assert.assertEquals("xyz", slice.get("c").getString());
	}

	@Test
	public void indexedObject() throws VPackException {
		// {"a": 12, "b": true, "c": "xyz"}
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Object));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(3, slice.getLength());
		Assert.assertEquals(12, slice.get("a").getInteger());
		Assert.assertEquals(true, slice.get("b").getBoolean());
		Assert.assertEquals("xyz", slice.get("c").getString());
	}

	@Test
	public void objectInObject() throws VPackException {
		// {"a":{"a1":1,"a2":2},"b":{"b1":1,"b2":1}}
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Object));
		{
			builder.add("a", new Value(ValueType.Object));
			builder.add("a1", new Value(1));
			builder.add("a2", new Value(2));
			builder.close();
		}
		{
			builder.add("b", new Value(ValueType.Object));
			builder.add("b1", new Value(1));
			builder.add("b2", new Value(2));
			builder.close();
		}
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(2, slice.getLength());
		{
			final Slice a = slice.get("a");
			Assert.assertTrue(a.isObject());
			Assert.assertEquals(2, a.getLength());
			Assert.assertEquals(1, a.get("a1").getInteger());
			Assert.assertEquals(2, a.get("a2").getInteger());
		}
		{
			final Slice b = slice.get("b");
			Assert.assertTrue(b.isObject());
			Assert.assertEquals(2, b.getLength());
			Assert.assertEquals(1, b.get("b1").getInteger());
			Assert.assertEquals(2, b.get("b2").getInteger());
		}
	}

	@Test
	public void objectInObjectInObject() throws VPackException {
		// {"a":{"b":{"c":{"d":true}}}
		final Builder builder = new Builder();
		builder.add(new Value(ValueType.Object));
		builder.add("a", new Value(ValueType.Object));
		builder.add("b", new Value(ValueType.Object));
		builder.add("c", new Value(ValueType.Object));
		builder.add("d", new Value(true));
		builder.close();
		builder.close();
		builder.close();
		builder.close();

		final Slice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(1, slice.getLength());
		final Slice a = slice.get("a");
		Assert.assertTrue(a.isObject());
		Assert.assertEquals(1, a.getLength());
		final Slice b = a.get("b");
		Assert.assertTrue(b.isObject());
		Assert.assertEquals(1, b.getLength());
		final Slice c = b.get("c");
		Assert.assertTrue(c.isObject());
		Assert.assertEquals(1, c.getLength());
		final Slice d = c.get("d");
		Assert.assertTrue(d.isBoolean());
		Assert.assertTrue(d.isTrue());
	}

}
