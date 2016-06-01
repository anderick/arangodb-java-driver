package com.arangodb.velocypack;

import java.math.BigInteger;
import java.util.Date;

import com.arangodb.velocypack.exception.VPackValueTypeException;
import com.arangodb.velocypack.util.BinaryUtil;
import com.arangodb.velocypack.util.DateUtil;
import com.arangodb.velocypack.util.NumberUtil;
import com.arangodb.velocypack.util.ObjectArrayUtil;
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
		return ValueLengthUtil.get(head()) - 1;
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
		return NumberUtil.toDouble(vpack, start + 1, length());
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
		return NumberUtil.toLong(vpack, start + 1, length());
	}

	public BigInteger getUInt() {
		if (!isUInt()) {
			throw new VPackValueTypeException(ValueType.UInt);
		}
		return NumberUtil.toBigInteger(vpack, start + 1, length());
	}

	public Date getUTCDate() {
		if (!isUTCDate()) {
			throw new VPackValueTypeException(ValueType.UTCDate);
		}
		return DateUtil.toDate(vpack, start + 1, length());
	}

	public String getString() {
		if (!isString()) {
			throw new VPackValueTypeException(ValueType.String);
		}
		final String string = isLongString() ? getLongString() : getShortString();
		return string;
	}

	private boolean isLongString() {
		return head() == ((byte) 0xbf);
	}

	private String getShortString() {
		return StringUtil.toString(vpack, start + 1, length());
	}

	private String getLongString() {
		return StringUtil.toString(vpack, start + 9, getLongStringLength());
	}

	private int getLongStringLength() {
		return (int) NumberUtil.toLong(vpack, start + 1, 8);
	}

	public int getStringLength() {
		if (!isString()) {
			throw new VPackValueTypeException(ValueType.String);
		}
		return isLongString() ? getLongStringLength() : head() - 0x40;
	}

	public byte[] getBinary() {
		if (!isBinary()) {
			throw new VPackValueTypeException(ValueType.Binary);
		}
		final byte[] binary = BinaryUtil.toBinary(vpack, start + 1 + head() - ((byte) 0xbf), getBinaryLength());
		return binary;
	}

	public int getBinaryLength() {
		if (!isBinary()) {
			throw new VPackValueTypeException(ValueType.Binary);
		}
		return (int) NumberUtil.toLong(vpack, start + 1, head() - ((byte) 0xbf));
	}

	/**
	 * @return the number of members for an Array or Object object
	 */
	public long getLength() {
		if (!isArray() && !isObject()) {
			throw new VPackValueTypeException(ValueType.Array, ValueType.Object);
		}
		final long length;
		final byte head = head();
		if (head == 0x01 || head == 0x0a) {
			// empty
			length = 0;
		} else if (head == 0x13 || head == 0x14) {
			// compact array or object
			final long end = NumberUtil.readVariableValueLength(vpack, start + 1, false);
			length = NumberUtil.readVariableValueLength(vpack, (int) (start + end - 1), true);
		} else {
			final int offsetsize = ObjectArrayUtil.getOffsetSize(head);
			final long end = NumberUtil.toLongReversed(vpack, start + 1, offsetsize);
			if (head <= 0x05) {
				// no offset table or length
				final int firstSubOffset = findDataOffset();
				final Slice first = new Slice(vpack, start + firstSubOffset);
				length = (end - firstSubOffset) / first.getByteSize();
			} else if (offsetsize < 8) {
				length = NumberUtil.toLongReversed(vpack, start + 1 + offsetsize, offsetsize);
			} else {
				length = NumberUtil.toLong(vpack, (int) (start + end - offsetsize), offsetsize);
			}
		}
		return length;
	}

	/**
	 * Must be called for a nonempty array or object at start():
	 */
	private int findDataOffset() {
		final int fsm = ObjectArrayUtil.getFirstSubMap(head());
		final int offset;
		if (fsm <= 2 && vpack[start + 2] != 0) {
			offset = 2;
		} else if (fsm <= 3 && vpack[start + 3] != 0) {
			offset = 3;
		} else if (fsm <= 5 && vpack[start + 6] != 0) {
			offset = 5;
		} else {
			offset = 9;
		}
		return offset;
	}

	private long getByteSize() {
		long size = 0;
		final int valueLength = ValueLengthUtil.get(head());
		if (valueLength != 0) {
			size = valueLength;
		} else {
			// TODO
			size = 0;
		}
		return size;
	}
}
