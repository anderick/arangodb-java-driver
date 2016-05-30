package com.arangodb.velocypack;

import java.math.BigInteger;
import java.util.Date;

import com.arangodb.velocypack.exception.VPackValueTypeException;
import com.arangodb.velocypack.util.DateUtil;
import com.arangodb.velocypack.util.NumberUtil;
import com.arangodb.velocypack.util.StringUtil;
import com.arangodb.velocypack.util.ValueLengthUtil;
import com.arangodb.velocypack.util.ValueType;
import com.arangodb.velocypack.util.ValueTypeUtil;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class Slice {

	private final byte[] vpack;
	private final int start;

	public Slice(final byte[] vpack) {
		this(vpack, 0);
	}

	private Slice(final byte[] vpack, final int start) {
		super();
		this.vpack = vpack;
		this.start = start;
	}

	private byte head() {
		return vpack[start];
	}

	private ValueType type() {
		return ValueTypeUtil.get(head());
	}

	private int length() {
		// TODO if 0 = calc
		return ValueLengthUtil.get(head());
	}

	private int valueLength() {
		return length() - 1;
	}

	private boolean isType(final ValueType type) {
		return type() == type;
	}

	public boolean isNone() {
		return isType(ValueType.None);
	}

	public boolean isNull() {
		return isType(ValueType.Null);
	}

	public boolean isIllegal() {
		return isType(ValueType.Illegal);
	}

	public boolean isBoolean() {
		return isType(ValueType.Bool);
	}

	public boolean isTrue() {
		return head() == 0x1a;
	}

	public boolean isFalse() {
		return head() == 0x19;
	}

	public boolean isArray() {
		return isType(ValueType.Array);
	}

	public boolean isObject() {
		return isType(ValueType.Object);
	}

	public boolean isDouble() {
		return isType(ValueType.Double);
	}

	public boolean isUTCDate() {
		return isType(ValueType.UTCDate);
	}

	public boolean isExternal() {
		return isType(ValueType.External);
	}

	public boolean isMinKey() {
		return isType(ValueType.MinKey);
	}

	public boolean isMaxKey() {
		return isType(ValueType.MaxKey);
	}

	public boolean isInt() {
		return isType(ValueType.Int);
	}

	public boolean isUInt() {
		return isType(ValueType.UInt);
	}

	public boolean isSmallInt() {
		return isType(ValueType.SmallInt);
	}

	public boolean isInteger() {
		return isInt() || isUInt() || isSmallInt();
	}

	public boolean isNumber() {
		return isInteger() || isDouble();
	}

	public boolean isString() {
		return isType(ValueType.String);
	}

	public boolean isBinary() {
		return isType(ValueType.Binary);
	}

	public boolean isBCD() {
		return isType(ValueType.BCD);
	}

	public boolean isCustom() {
		return isType(ValueType.Custom);
	}

	public boolean getBoolean() {
		if (!isBoolean()) {
			throw new VPackValueTypeException(ValueType.Bool);
		}
		return isTrue();
	}

	public double getDouble() {
		if (!isDouble()) {
			throw new VPackValueTypeException(ValueType.Double);
		}
		return NumberUtil.toDouble(vpack, start + 1, valueLength());
	}

	public long getSmallInt() {
		if (!isSmallInt()) {
			throw new VPackValueTypeException(ValueType.SmallInt);
		}
		final byte head = head();
		final long smallInt;
		if (head >= 0x30 && head <= 0x39) {
			smallInt = head - 0x30;
		} else /* if (head >= 0x3a && head <= 0x3f) */ {
			smallInt = head - 0x3a - 6;
		}
		return smallInt;
	}

	public long getInt() {
		if (!isInt()) {
			throw new VPackValueTypeException(ValueType.Int);
		}
		return NumberUtil.toLong(vpack, start + 1, valueLength());
	}

	public BigInteger getUInt() {
		if (!isUInt()) {
			throw new VPackValueTypeException(ValueType.UInt);
		}
		return NumberUtil.toBigInteger(vpack, start + 1, valueLength());
	}

	public Date getUTCDate() {
		if (!isUTCDate()) {
			throw new VPackValueTypeException(ValueType.UTCDate);
		}
		return DateUtil.toDate(vpack, start + 1, valueLength());
	}

	public String getString() {
		if (!isString()) {
			throw new VPackValueTypeException(ValueType.String);
		}
		final String string = (head() == ((byte) 0xbf)) ? getLongString() : getShortString();
		return string;
	}

	private String getShortString() {
		return StringUtil.toString(vpack, start + 1, valueLength());
	}

	private String getLongString() {
		final long length = NumberUtil.toLong(vpack, start + 1, 8);
		return StringUtil.toString(vpack, start + 9, (int) length);
	}

}
