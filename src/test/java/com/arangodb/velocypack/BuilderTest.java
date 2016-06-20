package com.arangodb.velocypack;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.arangodb.velocypack.VPackBuilder.BuilderOptions;
import com.arangodb.velocypack.exception.VPackBuilderUnexpectedValueException;
import com.arangodb.velocypack.exception.VPackBuilderException;
import com.arangodb.velocypack.exception.VPackBuilderNumberOutOfRangeException;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class BuilderTest {

	@Test
	public void addNull() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Null));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isNull());
	}

	@Test
	public void addBooleanTrue() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(true));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isBoolean());
		Assert.assertTrue(slice.getAsBoolean());
	}

	@Test
	public void addBooleanFalse() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(false));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isBoolean());
		Assert.assertFalse(slice.getAsBoolean());
	}

	@Test
	public void addBooleanNull() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final Boolean b = null;
		builder.add(new Value(b));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isNull());
	}

	@Test
	public void addDouble() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final double value = Double.MAX_VALUE;
		builder.add(new Value(value));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isDouble());
		Assert.assertEquals(value, slice.getAsDouble(), 0);
	}

	@Test
	public void addIntegerAsSmallIntMin() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final int value = -6;
		builder.add(new Value(value, ValueType.SmallInt));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getAsInt());
	}

	@Test
	public void addIntegerAsSmallIntMax() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final int value = 9;
		builder.add(new Value(value, ValueType.SmallInt));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getAsInt());
	}

	@Test(expected = VPackBuilderNumberOutOfRangeException.class)
	public void addIntegerAsSmallIntOutofRange() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final int value = Integer.MAX_VALUE;
		builder.add(new Value(value, ValueType.SmallInt));
	}

	@Test
	public void addLongAsSmallIntMin() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = -6;
		builder.add(new Value(value, ValueType.SmallInt));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getAsLong());
	}

	@Test
	public void addLongAsSmallIntMax() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = 9;
		builder.add(new Value(value, ValueType.SmallInt));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getAsLong());
	}

	@Test(expected = VPackBuilderNumberOutOfRangeException.class)
	public void addLongAsSmallIntOutofRange() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.SmallInt));
	}

	@Test
	public void addBigIntegerAsSmallIntMin() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(-6);
		builder.add(new Value(value, ValueType.SmallInt));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getAsBigInteger());
	}

	@Test
	public void addBigIntegerAsSmallIntMax() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(9);
		builder.add(new Value(value, ValueType.SmallInt));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isSmallInt());
		Assert.assertEquals(value, slice.getAsBigInteger());
	}

	@Test(expected = VPackBuilderNumberOutOfRangeException.class)
	public void addBigIntegerAsSmallIntOutofRange() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.SmallInt));
	}

	@Test
	public void addIntegerAsInt() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final int value = Integer.MAX_VALUE;
		builder.add(new Value(value, ValueType.Int));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isInt());
		Assert.assertEquals(value, slice.getAsInt());
	}

	@Test
	public void addLongAsInt() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.Int));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isInt());
		Assert.assertEquals(value, slice.getAsLong());
	}

	@Test
	public void addBigIntegerAsInt() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.Int));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isInt());
		Assert.assertEquals(value, slice.getAsBigInteger());
	}

	@Test
	public void addLongAsUInt() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.UInt));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isUInt());
		Assert.assertEquals(value, slice.getAsLong());
	}

	@Test
	public void addBigIntegerAsUInt() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.UInt));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isUInt());
		Assert.assertEquals(value, slice.getAsBigInteger());
	}

	@Test(expected = VPackBuilderUnexpectedValueException.class)
	public void addLongAsUIntNegative() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = -1;
		builder.add(new Value(value, ValueType.UInt));
	}

	@Test(expected = VPackBuilderUnexpectedValueException.class)
	public void addBigIntegerAsUIntNegative() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(-1);
		builder.add(new Value(value, ValueType.UInt));
	}

	@Test
	public void addUTCDate() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final Date date = new Date();
		builder.add(new Value(date));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isUTCDate());
		Assert.assertEquals(date, slice.getAsDate());
	}

	@Test
	public void addStringShort() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final String s = "Hallo Welt!";
		builder.add(new Value(s));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isString());
		Assert.assertEquals(s, slice.getAsString());
	}

	@Test
	public void addStringLong() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		final String s = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus.";
		builder.add(new Value(s));

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isString());
		Assert.assertEquals(s, slice.getAsString());
	}

	@Test
	public void emptyArray() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Array));
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(0, slice.getLength());
		Assert.assertEquals(1, slice.getVpack().length);
	}

	@Test
	public void compactArray() throws VPackBuilderException {
		final long[] expected = { 1, 16 };
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Array, true));
		for (final long l : expected) {
			builder.add(new Value(l));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(2, slice.getLength());
		for (int i = 0; i < expected.length; i++) {
			final VPackSlice at = slice.at(i);
			Assert.assertTrue(at.isInteger());
			Assert.assertEquals(expected[i], at.getAsLong());
		}
	}

	@Test
	public void unindexedArray() throws VPackBuilderException {
		final long[] expected = { 1, 16 };
		final BuilderOptions options = new BuilderOptions();
		options.setBuildUnindexedArrays(true);
		final VPackBuilder builder = new VPackBuilder(options);
		builder.add(new Value(ValueType.Array, false));
		for (final long l : expected) {
			builder.add(new Value(l));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(2, slice.getLength());
		for (int i = 0; i < expected.length; i++) {
			final VPackSlice at = slice.at(i);
			Assert.assertTrue(at.isInteger());
			Assert.assertEquals(expected[i], at.getAsLong());
		}
	}

	@Test
	public void indexedArray() throws VPackBuilderException {
		final long[] values = { 1, 2, 3 };
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Array));
		for (final long l : values) {
			builder.add(new Value(l));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(3, slice.getLength());
	}

	@Test
	public void indexedArray2ByteLength() throws VPackBuilderException {
		final long valueCount = 100;
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Array));
		for (long i = 0; i < valueCount; i++) {
			builder.add(new Value(
					"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus."));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertEquals(0x07, slice.head());
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(valueCount, slice.getLength());
	}

	@Test
	public void indexedArray4ByteLength() throws VPackBuilderException {
		final long valueCount = 200;
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Array));
		for (long i = 0; i < valueCount; i++) {
			builder.add(new Value(
					"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus."));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertEquals(0x08, slice.head());
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(valueCount, slice.getLength());
	}

	@Test
	public void arrayInArray() throws VPackBuilderException {
		final long[][] values = { { 1, 2, 3 }, { 1, 2, 3 } };
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Array));
		for (final long[] ls : values) {
			builder.add(new Value(ValueType.Array));
			for (final long l : ls) {
				builder.add(new Value(l));
			}
			builder.close();
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(values.length, slice.getLength());
		for (int i = 0; i < values.length; i++) {
			final VPackSlice ls = slice.at(i);
			Assert.assertTrue(ls.isArray());
			Assert.assertEquals(values[i].length, ls.getLength());
			for (int j = 0; j < values[i].length; j++) {
				final VPackSlice l = ls.at(j);
				Assert.assertTrue(l.isInteger());
				Assert.assertEquals(values[i][j], l.getAsLong());
			}
		}
	}

	@Test
	public void arrayInArrayInArray() throws VPackBuilderException {
		final long[][][] values = { { { 1, 2, 3 } } };
		final VPackBuilder builder = new VPackBuilder();
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

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isArray());
		Assert.assertEquals(values.length, slice.getLength());
		for (int i = 0; i < values.length; i++) {
			final VPackSlice lls = slice.at(i);
			Assert.assertTrue(lls.isArray());
			Assert.assertEquals(values[i].length, lls.getLength());
			for (int j = 0; j < values[i].length; j++) {
				final VPackSlice ls = lls.at(i);
				Assert.assertTrue(ls.isArray());
				Assert.assertEquals(values[i][j].length, ls.getLength());
				for (int k = 0; k < values[i][j].length; k++) {
					final VPackSlice l = ls.at(k);
					Assert.assertTrue(l.isInteger());
					Assert.assertEquals(values[i][j][k], l.getAsLong());
				}
			}

		}
	}

	@Test
	public void emptyObject() throws VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Object));
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(0, slice.getLength());
		Assert.assertEquals(1, slice.getVpack().length);
	}

	@Test
	public void compactObject() throws VPackBuilderException {
		// {"a": 12, "b": true, "c": "xyz"}
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Object, true));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(3, slice.getLength());
		Assert.assertEquals(12, slice.get("a").getAsLong());
		Assert.assertEquals(true, slice.get("b").getAsBoolean());
		Assert.assertEquals("xyz", slice.get("c").getAsString());
	}

	@Test
	public void unindexedObject() throws VPackBuilderException {
		// {"a": 12, "b": true, "c": "xyz"}
		final BuilderOptions options = new BuilderOptions();
		options.setBuildUnindexedObjects(true);
		final VPackBuilder builder = new VPackBuilder(options);
		builder.add(new Value(ValueType.Object, false));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(3, slice.getLength());
		Assert.assertEquals(12, slice.get("a").getAsLong());
		Assert.assertEquals(true, slice.get("b").getAsBoolean());
		Assert.assertEquals("xyz", slice.get("c").getAsString());
	}

	@Test
	public void indexedObject() throws VPackBuilderException {
		// {"a": 12, "b": true, "c": "xyz"}
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Object));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(3, slice.getLength());
		Assert.assertEquals(12, slice.get("a").getAsLong());
		Assert.assertEquals(true, slice.get("b").getAsBoolean());
		Assert.assertEquals("xyz", slice.get("c").getAsString());
	}

	@Test
	public void objectInObject() throws VPackBuilderException {
		// {"a":{"a1":1,"a2":2},"b":{"b1":1,"b2":1}}
		final VPackBuilder builder = new VPackBuilder();
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

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(2, slice.getLength());
		{
			final VPackSlice a = slice.get("a");
			Assert.assertTrue(a.isObject());
			Assert.assertEquals(2, a.getLength());
			Assert.assertEquals(1, a.get("a1").getAsLong());
			Assert.assertEquals(2, a.get("a2").getAsLong());
		}
		{
			final VPackSlice b = slice.get("b");
			Assert.assertTrue(b.isObject());
			Assert.assertEquals(2, b.getLength());
			Assert.assertEquals(1, b.get("b1").getAsLong());
			Assert.assertEquals(2, b.get("b2").getAsLong());
		}
	}

	@Test
	public void objectInObjectInObject() throws VPackBuilderException {
		// {"a":{"b":{"c":{"d":true}}}
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.Object));
		builder.add("a", new Value(ValueType.Object));
		builder.add("b", new Value(ValueType.Object));
		builder.add("c", new Value(ValueType.Object));
		builder.add("d", new Value(true));
		builder.close();
		builder.close();
		builder.close();
		builder.close();

		final VPackSlice slice = builder.slice();
		Assert.assertTrue(slice.isObject());
		Assert.assertEquals(1, slice.getLength());
		final VPackSlice a = slice.get("a");
		Assert.assertTrue(a.isObject());
		Assert.assertEquals(1, a.getLength());
		final VPackSlice b = a.get("b");
		Assert.assertTrue(b.isObject());
		Assert.assertEquals(1, b.getLength());
		final VPackSlice c = b.get("c");
		Assert.assertTrue(c.isObject());
		Assert.assertEquals(1, c.getLength());
		final VPackSlice d = c.get("d");
		Assert.assertTrue(d.isBoolean());
		Assert.assertTrue(d.isTrue());
	}

}
