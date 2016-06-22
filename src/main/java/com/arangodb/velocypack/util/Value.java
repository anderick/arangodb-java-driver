package com.arangodb.velocypack.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.arangodb.velocypack.exception.VPackValueTypeException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class Value {

	private Boolean b;
	private Double d;
	private Long l;
	private Integer i;
	private Float f;
	private Short sh;
	private BigInteger bi;
	private BigDecimal bd;
	private String s;
	private Character c;
	private Date date;

	private final ValueType type;
	private final Class<?> clazz;
	private final boolean unindexed;

	private Value(final ValueType type, final Class<?> clazz) {
		this(type, clazz, false);
	}

	private Value(final ValueType type, final Class<?> clazz, final boolean unindexed) {
		super();
		this.type = type;
		this.clazz = clazz;
		this.unindexed = unindexed;
	}

	/**
	 * creates a Value with the specified type Array or Object
	 * 
	 * @throws VPackValueTypeException
	 */
	public Value(final ValueType type) throws VPackValueTypeException {
		this(type, false);
	}

	/**
	 * creates a Value with the specified type Array or Object or Null
	 * 
	 * @throws VPackValueTypeException
	 */
	public Value(final ValueType type, final boolean unindexed) throws VPackValueTypeException {
		this(type, null, unindexed);
		if (type != ValueType.Array && type != ValueType.Object && type != ValueType.Null) {
			throw new VPackValueTypeException(ValueType.Array, ValueType.Object, ValueType.Null);
		}
	}

	public Value(final Boolean value) {
		this(checkNull(value, ValueType.Bool), Boolean.class);
		b = value;
	}

	public Value(final Double value) {
		this(checkNull(value, ValueType.Double), Double.class);
		d = value;
	}

	public Value(final Long value) {
		this(checkNull(value, ValueType.Int), Long.class);
		l = value;
	}

	public Value(final Long value, final ValueType type) throws VPackValueTypeException {
		this(checkNull(value, type), Long.class);
		if (type != ValueType.Int && type != ValueType.UInt && type != ValueType.SmallInt) {
			throw new VPackValueTypeException(ValueType.Int, ValueType.UInt, ValueType.SmallInt);
		}
		l = value;
	}

	public Value(final Integer value) {
		this(checkNull(value, ValueType.Int), Integer.class);
		i = value;
	}

	public Value(final Integer value, final ValueType type) throws VPackValueTypeException {
		this(checkNull(value, type), Integer.class);
		if (type != ValueType.Int && type != ValueType.UInt && type != ValueType.SmallInt) {
			throw new VPackValueTypeException(ValueType.Int, ValueType.UInt, ValueType.SmallInt);
		}
		i = value;
	}

	public Value(final Float value) {
		this(checkNull(value, ValueType.Double), Float.class);
		f = value;
	}

	public Value(final Short value) {
		this(checkNull(value, ValueType.Int), Short.class);
		sh = value;
	}

	public Value(final BigInteger value) {
		this(checkNull(value, ValueType.Int), BigInteger.class);
		bi = value;
	}

	public Value(final BigInteger value, final ValueType type) throws VPackValueTypeException {
		this(checkNull(value, type), BigInteger.class);
		if (type != ValueType.Int && type != ValueType.UInt && type != ValueType.SmallInt) {
			throw new VPackValueTypeException(ValueType.Int, ValueType.UInt, ValueType.SmallInt);
		}
		bi = value;
	}

	public Value(final BigDecimal value) {
		this(checkNull(value, ValueType.Double), BigDecimal.class);
		bd = value;
	}

	public Value(final String value) {
		this(checkNull(value, ValueType.String), String.class);
		s = value;
	}

	public Value(final Character value) {
		this(checkNull(value, ValueType.String), Character.class);
		c = value;
	}

	public Value(final Date value) {
		this(checkNull(value, ValueType.UTCDate), Date.class);
		date = value;
	}

	private static ValueType checkNull(final Object obj, final ValueType type) {
		return obj != null ? type : ValueType.Null;
	}

	public ValueType getType() {
		return type;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public boolean isUnindexed() {
		return unindexed;
	}

	public Boolean getBoolean() {
		return b;
	}

	public Double getDouble() {
		return d;
	}

	public Long getLong() {
		return l;
	}

	public Integer getInteger() {
		return i;
	}

	public Float getFloat() {
		return f;
	}

	public Short getShort() {
		return sh;
	}

	public BigInteger getBigInteger() {
		return bi;
	}

	public BigDecimal getBigDecimal() {
		return bd;
	}

	public String getString() {
		return s;
	}

	public Character getCharacter() {
		return c;
	}

	public Date getDate() {
		return date;
	}

}
