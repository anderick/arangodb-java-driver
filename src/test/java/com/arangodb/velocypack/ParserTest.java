package com.arangodb.velocypack;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import com.arangodb.velocypack.exception.VPackBuilderException;
import com.arangodb.velocypack.exception.VPackParserException;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class ParserTest {

	protected static class TestEntityBoolean {
		private boolean a = true;
		private boolean b = false;
		private Boolean c = Boolean.TRUE;
		private Boolean d = Boolean.FALSE;

		public boolean isA() {
			return a;
		}

		public void setA(final boolean a) {
			this.a = a;
		}

		public boolean isB() {
			return b;
		}

		public void setB(final boolean b) {
			this.b = b;
		}

		public Boolean getC() {
			return c;
		}

		public void setC(final Boolean c) {
			this.c = c;
		}

		public Boolean getD() {
			return d;
		}

		public void setD(final Boolean d) {
			this.d = d;
		}
	}

	@Test
	public void fromBoolean() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityBoolean());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice a = vpack.get("a");
			Assert.assertTrue(a.isBoolean());
			Assert.assertTrue(a.getAsBoolean());
		}
		{
			final VPackSlice b = vpack.get("b");
			Assert.assertTrue(b.isBoolean());
			Assert.assertFalse(b.getAsBoolean());
		}
		{
			final VPackSlice c = vpack.get("c");
			Assert.assertTrue(c.isBoolean());
			Assert.assertTrue(c.getAsBoolean());
		}
		{
			final VPackSlice d = vpack.get("d");
			Assert.assertTrue(d.isBoolean());
			Assert.assertFalse(d.getAsBoolean());
		}
	}

	@Test
	public void toBoolean() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("a", new Value(false));
			builder.add("b", new Value(true));
			builder.add("c", new Value(Boolean.FALSE));
			builder.add("d", new Value(Boolean.TRUE));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityBoolean entity = parser.toEntity(vpack, TestEntityBoolean.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(false, entity.a);
		Assert.assertEquals(true, entity.b);
		Assert.assertEquals(Boolean.FALSE, entity.c);
		Assert.assertEquals(Boolean.TRUE, entity.d);
	}

	protected static class TestEntityString {
		private String s = "test";
		private Character c1 = 't';
		private char c2 = 't';

		public String getS() {
			return s;
		}

		public void setS(final String s) {
			this.s = s;
		}

		public Character getC1() {
			return c1;
		}

		public void setC1(final Character c1) {
			this.c1 = c1;
		}

		public char getC2() {
			return c2;
		}

		public void setC2(final char c2) {
			this.c2 = c2;
		}
	}

	@Test
	public void fromStrings() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityString());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice s = vpack.get("s");
			Assert.assertTrue(s.isString());
			Assert.assertEquals("test", s.getAsString());
		}
		{
			final VPackSlice c1 = vpack.get("c1");
			Assert.assertTrue(c1.isString());
			Assert.assertEquals('t', c1.getAsChar());
		}
		{
			final VPackSlice c2 = vpack.get("c2");
			Assert.assertTrue(c2.isString());
			Assert.assertEquals('t', c2.getAsChar());
		}
	}

	@Test
	public void toStrings() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("s", new Value("abc"));
			builder.add("c1", new Value('d'));
			builder.add("c2", new Value('d'));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityString entity = parser.toEntity(vpack, TestEntityString.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals("abc", entity.s);
		Assert.assertEquals(new Character('d'), entity.c1);
		Assert.assertEquals(new Character('d'), entity.c1);
	}

	protected static class TestEntityInteger {
		private int i1 = 1;
		private Integer i2 = 1;

		public int getI1() {
			return i1;
		}

		public void setI1(final int i1) {
			this.i1 = i1;
		}

		public Integer getI2() {
			return i2;
		}

		public void setI2(final Integer i2) {
			this.i2 = i2;
		}
	}

	@Test
	public void fromInteger() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityInteger());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice i1 = vpack.get("i1");
			Assert.assertTrue(i1.isInteger());
			Assert.assertEquals(1, i1.getAsInt());
		}
		{
			final VPackSlice i2 = vpack.get("i2");
			Assert.assertTrue(i2.isInteger());
			Assert.assertEquals(1, i2.getAsInt());
		}
	}

	@Test
	public void toInteger() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("i1", new Value(2));
			builder.add("i2", new Value(3));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityInteger entity = parser.toEntity(vpack, TestEntityInteger.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(2, entity.i1);
		Assert.assertEquals(new Integer(3), entity.i2);
	}

	protected static class TestEntityLong {
		private long l1 = 1;
		private Long l2 = 1L;

		public long getL1() {
			return l1;
		}

		public void setL1(final long l1) {
			this.l1 = l1;
		}

		public Long getL2() {
			return l2;
		}

		public void setL2(final Long l2) {
			this.l2 = l2;
		}
	}

	@Test
	public void fromLong() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityLong());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice l1 = vpack.get("l1");
			Assert.assertTrue(l1.isInteger());
			Assert.assertEquals(1, l1.getAsLong());
		}
		{
			final VPackSlice l2 = vpack.get("l2");
			Assert.assertTrue(l2.isInteger());
			Assert.assertEquals(1, l2.getAsLong());
		}
	}

	@Test
	public void toLong() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("l1", new Value(2));
			builder.add("l2", new Value(3));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityLong entity = parser.toEntity(vpack, TestEntityLong.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(2, entity.l1);
		Assert.assertEquals(new Long(3), entity.l2);
	}

	protected static class TestEntityFloat {
		private float f1 = 1;
		private Float f2 = 1F;

		public float getF1() {
			return f1;
		}

		public void setF1(final float f1) {
			this.f1 = f1;
		}

		public Float getF2() {
			return f2;
		}

		public void setF2(final Float f2) {
			this.f2 = f2;
		}
	}

	@Test
	public void fromFloat() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityFloat());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice f1 = vpack.get("f1");
			Assert.assertTrue(f1.isInteger());
			Assert.assertEquals(1, f1.getAsFloat(), 0.);
		}
		{
			final VPackSlice f2 = vpack.get("f2");
			Assert.assertTrue(f2.isInteger());
			Assert.assertEquals(1, f2.getAsFloat(), 0.);
		}
	}

	@Test
	public void toFloat() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("f1", new Value(2));
			builder.add("f2", new Value(3));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityFloat entity = parser.toEntity(vpack, TestEntityFloat.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(2, entity.f1, 0.);
		Assert.assertEquals(new Float(3), entity.f2);
	}

	protected static class TestEntityShort {
		private short s1 = 1;
		private Short s2 = 1;

		public short getS1() {
			return s1;
		}

		public void setS1(final short s1) {
			this.s1 = s1;
		}

		public Short getS2() {
			return s2;
		}

		public void setS2(final Short s2) {
			this.s2 = s2;
		}
	}

	@Test
	public void fromShort() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityShort());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice s1 = vpack.get("s1");
			Assert.assertTrue(s1.isInteger());
			Assert.assertEquals(1, s1.getAsShort());
		}
		{
			final VPackSlice s2 = vpack.get("s2");
			Assert.assertTrue(s2.isInteger());
			Assert.assertEquals(1, s2.getAsShort());
		}
	}

	@Test
	public void toShort() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("s1", new Value(2));
			builder.add("s2", new Value(3));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityShort entity = parser.toEntity(vpack, TestEntityShort.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(2, entity.s1);
		Assert.assertEquals(new Short((short) 3), entity.s2);
	}

	protected static class TestEntityDouble {
		private Double d1 = 1.5;
		private double d2 = 1.5;

		public Double getD1() {
			return d1;
		}

		public void setD1(final Double d1) {
			this.d1 = d1;
		}

		public double getD2() {
			return d2;
		}

		public void setD2(final double d2) {
			this.d2 = d2;
		}
	}

	@Test
	public void fromDouble() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityDouble());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice d1 = vpack.get("d1");
			Assert.assertTrue(d1.isDouble());
			Assert.assertEquals(1.5, d1.getAsDouble(), 0.);
		}
		{
			final VPackSlice d2 = vpack.get("d2");
			Assert.assertTrue(d2.isDouble());
			Assert.assertEquals(1.5, d2.getAsDouble(), 0.);
		}
	}

	@Test
	public void toDouble() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("d1", new Value(2.25));
			builder.add("d2", new Value(3.75));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityDouble entity = parser.toEntity(vpack, TestEntityDouble.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(2.25, entity.d1, 0.);
		Assert.assertEquals(3.75, entity.d2, 0.);
	}

	protected static class TestEntityBigNumber {
		private BigInteger bi = BigInteger.valueOf(1L);
		private BigDecimal bd = BigDecimal.valueOf(1.5);

		public BigInteger getBi() {
			return bi;
		}

		public void setBi(final BigInteger bi) {
			this.bi = bi;
		}

		public BigDecimal getBd() {
			return bd;
		}

		public void setBd(final BigDecimal bd) {
			this.bd = bd;
		}
	}

	@Test
	public void fromBigNumbers() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityBigNumber());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice bi = vpack.get("bi");
			Assert.assertTrue(bi.isInteger());
			Assert.assertEquals(BigInteger.valueOf(1L), bi.getAsBigInteger());
		}
		{
			final VPackSlice bd = vpack.get("bd");
			Assert.assertTrue(bd.isDouble());
			Assert.assertEquals(BigDecimal.valueOf(1.5), bd.getAsBigDecimal());
		}
	}

	@Test
	public void toBigNumbers() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("bi", new Value(BigInteger.valueOf(2)));
			builder.add("bd", new Value(BigDecimal.valueOf(3.75)));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityBigNumber entity = parser.toEntity(vpack, TestEntityBigNumber.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(BigInteger.valueOf(2), entity.bi);
		Assert.assertEquals(BigDecimal.valueOf(3.75), entity.bd);
	}

	protected static class TestEntityArray {
		private String[] a1 = { "a", "b", "cd" };
		private int[] a2 = { 1, 2, 3, 4, 5 };
		private boolean[] a3 = { true, true, false };
		private TestEnum[] a4 = TestEnum.values();

		public String[] getA1() {
			return a1;
		}

		public void setA1(final String[] a1) {
			this.a1 = a1;
		}

		public int[] getA2() {
			return a2;
		}

		public void setA2(final int[] a2) {
			this.a2 = a2;
		}

		public boolean[] getA3() {
			return a3;
		}

		public void setA3(final boolean[] a3) {
			this.a3 = a3;
		}

		public TestEnum[] getA4() {
			return a4;
		}

		public void setA4(final TestEnum[] a4) {
			this.a4 = a4;
		}

	}

	@Test
	public void fromArray() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final TestEntityArray entity = new TestEntityArray();
		final VPackSlice vpack = parser.fromEntity(entity);
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice a1 = vpack.get("a1");
			Assert.assertTrue(a1.isArray());
			Assert.assertEquals(entity.a1.length, a1.getLength());
			for (int i = 0; i < a1.getLength(); i++) {
				Assert.assertEquals(entity.a1[i], a1.at(i).getAsString());
			}
		}
		{
			final VPackSlice a2 = vpack.get("a2");
			Assert.assertTrue(a2.isArray());
			Assert.assertEquals(entity.a2.length, a2.getLength());
			for (int i = 0; i < a2.getLength(); i++) {
				Assert.assertEquals(entity.a2[i], a2.at(i).getAsInt());
			}
		}
		{
			final VPackSlice a3 = vpack.get("a3");
			Assert.assertTrue(a3.isArray());
			Assert.assertEquals(entity.a3.length, a3.getLength());
			for (int i = 0; i < a3.getLength(); i++) {
				Assert.assertEquals(entity.a3[i], a3.at(i).getAsBoolean());
			}
		}
		{
			final VPackSlice a4 = vpack.get("a4");
			Assert.assertTrue(a4.isArray());
			Assert.assertEquals(entity.a4.length, a4.getLength());
			for (int i = 0; i < a4.getLength(); i++) {
				Assert.assertEquals(entity.a4[i], TestEnum.valueOf(a4.at(i).getAsString()));
			}
		}
	}

	@Test
	public void toArray() throws VPackParserException, VPackBuilderException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			{
				builder.add("a1", new Value(ValueType.Array));
				builder.add(new Value("a"));
				builder.add(new Value("b"));
				builder.add(new Value("c"));
				builder.close();
			}
			{
				builder.add("a2", new Value(ValueType.Array));
				builder.add(new Value(1));
				builder.add(new Value(2));
				builder.add(new Value(3));
				builder.add(new Value(4));
				builder.close();
			}
			{
				builder.add("a3", new Value(ValueType.Array));
				builder.add(new Value(false));
				builder.add(new Value(true));
				builder.close();
			}
			{
				builder.add("a4", new Value(ValueType.Array));
				builder.add(new Value(TestEnum.A.name()));
				builder.add(new Value(TestEnum.B.name()));
				builder.close();
			}
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityArray entity = parser.toEntity(vpack, TestEntityArray.class);
		Assert.assertNotNull(entity);
		{
			Assert.assertEquals(3, entity.a1.length);
			Assert.assertEquals("a", entity.a1[0]);
			Assert.assertEquals("b", entity.a1[1]);
			Assert.assertEquals("c", entity.a1[2]);
		}
		{
			Assert.assertEquals(4, entity.a2.length);
			Assert.assertEquals(1, entity.a2[0]);
			Assert.assertEquals(2, entity.a2[1]);
			Assert.assertEquals(3, entity.a2[2]);
			Assert.assertEquals(4, entity.a2[3]);
		}
		{
			Assert.assertEquals(2, entity.a3.length);
			Assert.assertEquals(false, entity.a3[0]);
			Assert.assertEquals(true, entity.a3[1]);
		}
		{
			Assert.assertEquals(2, entity.a4.length);
			Assert.assertEquals(TestEnum.A, entity.a4[0]);
			Assert.assertEquals(TestEnum.B, entity.a4[1]);
		}
	}

	protected enum TestEnum {
		A, B, C
	}

	protected static class TestEntityEnum {
		private TestEnum e1 = TestEnum.A;

		public TestEnum getE1() {
			return e1;
		}

		public void setE1(final TestEnum e1) {
			this.e1 = e1;
		}
	}

	@Test
	public void fromEnum() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityEnum());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice e1 = vpack.get("e1");
			Assert.assertTrue(e1.isString());
			Assert.assertEquals(TestEnum.A, TestEnum.valueOf(e1.getAsString()));
		}
	}

	@Test
	public void toEnum() throws VPackBuilderException, VPackParserException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("e1", new Value(TestEnum.B.name()));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityEnum entity = parser.toEntity(vpack, TestEntityEnum.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(TestEnum.B, entity.e1);
	}

	protected static class TestEntityObject {
		private TestEntityLong o1 = new TestEntityLong();
		private TestEntityArray o2 = new TestEntityArray();

		public TestEntityLong getO1() {
			return o1;
		}

		public void setO1(final TestEntityLong o1) {
			this.o1 = o1;
		}

		public TestEntityArray getO2() {
			return o2;
		}

		public void setO2(final TestEntityArray o2) {
			this.o2 = o2;
		}
	}

	@Test
	public void fromObject() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final TestEntityObject entity = new TestEntityObject();
		final VPackSlice vpack = parser.fromEntity(entity);
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice o1 = vpack.get("o1");
			Assert.assertTrue(o1.isObject());
			{
				final VPackSlice l1 = o1.get("l1");
				Assert.assertTrue(l1.isInteger());
				Assert.assertEquals(1, l1.getAsLong());
			}
			{
				final VPackSlice l2 = o1.get("l2");
				Assert.assertTrue(l2.isInteger());
				Assert.assertEquals(1, l2.getAsLong());
			}
		}
		{
			final VPackSlice o2 = vpack.get("o2");
			Assert.assertTrue(o2.isObject());
			{
				final VPackSlice a1 = o2.get("a1");
				Assert.assertTrue(a1.isArray());
				Assert.assertEquals(entity.o2.a1.length, a1.getLength());
				for (int i = 0; i < a1.getLength(); i++) {
					Assert.assertEquals(entity.o2.a1[i], a1.at(i).getAsString());
				}
			}
			{
				final VPackSlice a2 = o2.get("a2");
				Assert.assertTrue(a2.isArray());
				Assert.assertEquals(entity.o2.a2.length, a2.getLength());
				for (int i = 0; i < a2.getLength(); i++) {
					Assert.assertEquals(entity.o2.a2[i], a2.at(i).getAsInt());
				}
			}
			{
				final VPackSlice a3 = o2.get("a3");
				Assert.assertTrue(a3.isArray());
				Assert.assertEquals(entity.o2.a3.length, a3.getLength());
				for (int i = 0; i < a3.getLength(); i++) {
					Assert.assertEquals(entity.o2.a3[i], a3.at(i).getAsBoolean());
				}
			}
			{
				final VPackSlice a4 = o2.get("a4");
				Assert.assertTrue(a4.isArray());
				Assert.assertEquals(entity.o2.a4.length, a4.getLength());
				for (int i = 0; i < a4.getLength(); i++) {
					Assert.assertEquals(entity.o2.a4[i], TestEnum.valueOf(a4.at(i).getAsString()));
				}
			}
		}
	}

	@Test
	public void toObject() throws VPackBuilderException, VPackParserException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			{
				builder.add("o1", new Value(ValueType.Object));
				builder.add("l1", new Value(5L));
				builder.add("l2", new Value(5L));
				builder.close();
			}
			{
				builder.add("o2", new Value(ValueType.Object));
				{
					builder.add("a1", new Value(ValueType.Array));
					builder.add(new Value("a"));
					builder.add(new Value("b"));
					builder.add(new Value("c"));
					builder.close();
				}
				{
					builder.add("a2", new Value(ValueType.Array));
					builder.add(new Value(1));
					builder.add(new Value(2));
					builder.add(new Value(3));
					builder.add(new Value(4));
					builder.close();
				}
				{
					builder.add("a3", new Value(ValueType.Array));
					builder.add(new Value(false));
					builder.add(new Value(true));
					builder.close();
				}
				{
					builder.add("a4", new Value(ValueType.Array));
					builder.add(new Value(TestEnum.A.name()));
					builder.add(new Value(TestEnum.B.name()));
					builder.close();
				}
				builder.close();
			}
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityObject entity = parser.toEntity(vpack, TestEntityObject.class);
		Assert.assertNotNull(entity);
		{
			Assert.assertEquals(5, entity.o1.l1);
			Assert.assertEquals(new Long(5), entity.o1.l2);
		}
		{
			Assert.assertEquals(3, entity.o2.a1.length);
			Assert.assertEquals("a", entity.o2.a1[0]);
			Assert.assertEquals("b", entity.o2.a1[1]);
			Assert.assertEquals("c", entity.o2.a1[2]);
		}
		{
			Assert.assertEquals(4, entity.o2.a2.length);
			Assert.assertEquals(1, entity.o2.a2[0]);
			Assert.assertEquals(2, entity.o2.a2[1]);
			Assert.assertEquals(3, entity.o2.a2[2]);
			Assert.assertEquals(4, entity.o2.a2[3]);
		}
		{
			Assert.assertEquals(2, entity.o2.a3.length);
			Assert.assertEquals(false, entity.o2.a3[0]);
			Assert.assertEquals(true, entity.o2.a3[1]);
		}
		{
			Assert.assertEquals(2, entity.o2.a4.length);
			Assert.assertEquals(TestEnum.A, entity.o2.a4[0]);
			Assert.assertEquals(TestEnum.B, entity.o2.a4[1]);
		}
	}

	protected static class TestEntityArrayInArray {
		private long[][] a1 = { { 1, 2, 3 }, { 4, 5, 6 } };

		public long[][] getA1() {
			return a1;
		}

		public void setA1(final long[][] a1) {
			this.a1 = a1;
		}
	}

	@Test
	public void fromArrayInArray() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final TestEntityArrayInArray entity = new TestEntityArrayInArray();
		final VPackSlice vpack = parser.fromEntity(entity);
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice a1 = vpack.get("a1");
			Assert.assertTrue(a1.isArray());
			Assert.assertEquals(entity.a1.length, a1.getLength());
			for (int i = 0; i < a1.getLength(); i++) {
				final VPackSlice at = a1.at(i);
				Assert.assertTrue(at.isArray());
				Assert.assertEquals(entity.a1[i].length, at.getLength());
				for (int j = 0; j < at.getLength(); j++) {
					final VPackSlice atat = at.at(j);
					Assert.assertTrue(atat.isInteger());
					Assert.assertEquals(entity.a1[i][j], atat.getAsLong());
				}
			}
		}
	}

	@Test
	public void toArrayInArray() throws VPackBuilderException, VPackParserException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			{
				builder.add(new Value(ValueType.Array));
				{
					builder.add(new Value(ValueType.Array));
					builder.add(new Value(1));
					builder.add(new Value(2));
					builder.add(new Value(3));
					builder.close();
				}
				{
					builder.add(new Value(ValueType.Array));
					builder.add(new Value(4));
					builder.add(new Value(5));
					builder.add(new Value(6));
					builder.close();
				}
				builder.close();
			}
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityArrayInArray entity = parser.toEntity(vpack, TestEntityArrayInArray.class);
		Assert.assertNotNull(entity);
		Assert.assertEquals(2, entity.a1.length);
		{
			Assert.assertEquals(3, entity.a1[0].length);
			Assert.assertEquals(1, entity.a1[0][0]);
			Assert.assertEquals(2, entity.a1[0][1]);
			Assert.assertEquals(3, entity.a1[0][2]);
		}
		{
			Assert.assertEquals(3, entity.a1[1].length);
			Assert.assertEquals(4, entity.a1[1][0]);
			Assert.assertEquals(5, entity.a1[1][1]);
			Assert.assertEquals(6, entity.a1[1][2]);
		}
	}

	protected static class TestEntityA {
		private String a = "a";

		public String getA() {
			return a;
		}

		public void setA(final String a) {
			this.a = a;
		}
	}

	protected static class TestEntityB extends TestEntityA {
		private String b = "b";

		public String getB() {
			return b;
		}

		public void setB(final String b) {
			this.b = b;
		}
	}

	@Test
	public void fromInheritance() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final VPackSlice vpack = parser.fromEntity(new TestEntityB());
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		Assert.assertEquals(2, vpack.getLength());
		{
			final VPackSlice a = vpack.get("a");
			Assert.assertTrue(a.isString());
			Assert.assertEquals("a", a.getAsString());
		}
		{
			final VPackSlice b = vpack.get("b");
			Assert.assertTrue(b.isString());
			Assert.assertEquals("b", b.getAsString());
		}
	}

	@Test
	public void toInheritance() throws VPackBuilderException, VPackParserException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("a", new Value("test"));
			builder.add("b", new Value("test"));
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		{
			final TestEntityA entity = parser.toEntity(vpack, TestEntityA.class);
			Assert.assertNotNull(entity);
			Assert.assertEquals("test", entity.getA());
		}
		{
			final TestEntityB entity = parser.toEntity(vpack, TestEntityB.class);
			Assert.assertNotNull(entity);
			Assert.assertEquals("test", entity.getA());
			Assert.assertEquals("test", entity.getB());
		}
	}

	protected static class TestEntityC {
		private TestEntityD d;

		public TestEntityD getD() {
			return d;
		}

		public void setD(final TestEntityD d) {
			this.d = d;
		}
	}

	protected static interface TestEntityD {
		String getD();

		void setD(String d);
	}

	protected static class TestEntityDImpl implements TestEntityD {
		private String d = "d";

		@Override
		public String getD() {
			return d;
		}

		@Override
		public void setD(final String d) {
			this.d = d;
		}
	}

	@Test
	public void fromInterface() throws VPackParserException {
		final VPackParser parser = new VPackParser();
		final TestEntityC entity = new TestEntityC();
		entity.setD(new TestEntityDImpl());
		final VPackSlice vpack = parser.fromEntity(entity);
		Assert.assertNotNull(vpack);
		Assert.assertTrue(vpack.isObject());
		{
			final VPackSlice d = vpack.get("d");
			Assert.assertTrue(d.isObject());
			final VPackSlice dd = d.get("d");
			Assert.assertTrue(dd.isString());
			Assert.assertEquals("d", dd.getAsString());
		}
	}

	@Test
	public void toInterface() throws VPackBuilderException, VPackParserException {
		final VPackBuilder builder = new VPackBuilder();
		{
			builder.add(new Value(ValueType.Object));
			builder.add("d", new Value(ValueType.Object));
			builder.add("d", new Value("test"));
			builder.close();
			builder.close();
		}
		final VPackSlice vpack = builder.slice();
		final VPackParser parser = new VPackParser();
		final TestEntityC entity = parser.toEntity(vpack, TestEntityC.class);
		Assert.assertNotNull(entity);
		// TODO
		// Assert.assertNotNull(entity.d);
		// Assert.assertEquals("test", entity.d.getD());
	}
}
