package com.arangodb.velocypack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;

import com.arangodb.velocypack.exception.VPackValueTypeException;
import com.arangodb.velocypack.util.ArrayIterator;
import com.arangodb.velocypack.util.BinaryUtil;
import com.arangodb.velocypack.util.DateUtil;
import com.arangodb.velocypack.util.NumberUtil;
import com.arangodb.velocypack.util.ObjectArrayUtil;
import com.arangodb.velocypack.util.ObjectIterator;
import com.arangodb.velocypack.util.StringUtil;
import com.arangodb.velocypack.util.ValueLengthUtil;
import com.arangodb.velocypack.util.ValueType;
import com.arangodb.velocypack.util.ValueTypeUtil;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackSlice {

	private final byte[] vpack;
	private final int start;

	protected VPackSlice() {
		this(new byte[] { 0x00 }, 0);
	}

	public VPackSlice(final byte[] vpack) {
		this(vpack, 0);
	}

	public VPackSlice(final byte[] vpack, final int start) {
		super();
		this.vpack = vpack;
		this.start = start;
	}

	public byte head() {
		return vpack[start];
	}

	public byte[] getVpack() {
		return vpack;
	}

	public int getStart() {
		return start;
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

	public boolean getAsBoolean() {
		if (!isBoolean()) {
			throw new VPackValueTypeException(ValueType.Bool);
		}
		return isTrue();
	}

	public double getAsDouble() {
		if (!isDouble()) {
			throw new VPackValueTypeException(ValueType.Double);
		}
		return NumberUtil.toDouble(vpack, start + 1, length());
	}

	public BigDecimal getAsBigDecimal() {
		return new BigDecimal(getAsDouble());
	}

	private long getSmallInt() {
		final byte head = head();
		final long smallInt;
		if (head >= 0x30 && head <= 0x39) {
			smallInt = head - 0x30;
		} else /* if (head >= 0x3a && head <= 0x3f) */ {
			smallInt = head - 0x3a - 6;
		}
		return smallInt;
	}

	private long getInt() {
		return NumberUtil.toLong(vpack, start + 1, length());
	}

	private long getUInt() {
		return NumberUtil.toLong(vpack, start + 1, length());
	}

	public Number getAsNumber() {
		final Number result;
		if (isSmallInt()) {
			result = getSmallInt();
		} else if (isInt()) {
			result = getInt();
		} else if (isUInt()) {
			result = getUInt();
		} else {
			throw new VPackValueTypeException(ValueType.Int, ValueType.UInt, ValueType.SmallInt);
		}
		return result;
	}

	public long getAsLong() {
		return getAsNumber().longValue();
	}

	public int getAsInt() {
		return getAsNumber().intValue();
	}

	public float getAsFloat() {
		return getAsNumber().floatValue();
	}

	public short getAsShort() {
		return getAsNumber().shortValue();
	}

	public BigInteger getAsBigInteger() {
		if (isSmallInt() || isInt()) {
			return BigInteger.valueOf(getAsLong());
		} else if (isUInt()) {
			return NumberUtil.toBigInteger(vpack, start + 1, length());
		} else {
			throw new VPackValueTypeException(ValueType.Int, ValueType.UInt, ValueType.SmallInt);
		}
	}

	public Date getAsDate() {
		if (!isUTCDate()) {
			throw new VPackValueTypeException(ValueType.UTCDate);
		}
		return DateUtil.toDate(vpack, start + 1, length());
	}

	public String getAsString() {
		if (!isString()) {
			throw new VPackValueTypeException(ValueType.String);
		}
		final String string = isLongString() ? getLongString() : getShortString();
		return string;
	}

	public char getAsChar() {
		// TODO
		return getAsString().charAt(0);
	}

	private boolean isLongString() {
		return head() == (byte) 0xbf;
	}

	private String getShortString() {
		return StringUtil.toString(vpack, start + 1, length());
	}

	private String getLongString() {
		return StringUtil.toString(vpack, start + 9, getLongStringLength());
	}

	private int getLongStringLength() {
		return (int) NumberUtil.toLongReversed(vpack, start + 1, 8);
	}

	private int getStringLength() {
		return isLongString() ? getLongStringLength() : head() - 0x40;
	}

	public byte[] getAsBinary() {
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
		return getBinaryLengthUnchecked();
	}

	private int getBinaryLengthUnchecked() {
		return (int) NumberUtil.toLong(vpack, start + 1, head() - ((byte) 0xbf));
	}

	/**
	 * @return the number of members for an Array, Object or String
	 */
	public long getLength() {
		final long length;
		if (isString()) {
			length = getStringLength();
		} else if (!isArray() && !isObject()) {
			throw new VPackValueTypeException(ValueType.Array, ValueType.Object, ValueType.String);
		} else {
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
					// array with no offset table or length
					final int dataOffset = findDataOffset();
					final VPackSlice first = new VPackSlice(vpack, start + dataOffset);
					length = (end - dataOffset) / first.getByteSize();
				} else if (offsetsize < 8) {
					length = NumberUtil.toLongReversed(vpack, start + 1 + offsetsize, offsetsize);
				} else {
					length = NumberUtil.toLongReversed(vpack, (int) (start + end - offsetsize), offsetsize);
				}
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

	public long getByteSize() {
		final long size;
		final byte head = head();
		final int valueLength = ValueLengthUtil.get(head);
		if (valueLength != 0) {
			size = valueLength;
		} else {
			switch (type()) {
			case Array:
			case Object:
				if (head == 0x13 || head == 0x14) {
					// compact Array or Object
					size = NumberUtil.readVariableValueLength(vpack, start + 1, false);
				} else /* if (head <= 0x14) */ {
					size = NumberUtil.toLong(vpack, start + 1, ObjectArrayUtil.getOffsetSize(head));
				}
				break;
			case String:
				// long UTF-8 String
				size = getLongStringLength() + 1 + 8;
				break;
			case Binary:
				size = 1 + head - getBinaryLengthUnchecked();
				break;
			case BCD:
				if (head <= 0xcf) {
					size = 1 + head + ((byte) 0xc7) + NumberUtil.toLong(vpack, start + 1, head - ((byte) 0xc7));
				} else {
					size = 1 + head - ((byte) 0xcf) + NumberUtil.toLong(vpack, start + 1, head - ((byte) 0xcf));
				}
				break;
			case Custom:
				if (head == 0xf4 || head == 0xf5 || head == 0xf6) {
					size = 2 + NumberUtil.toLong(vpack, start + 1, 1);
				} else if (head == 0xf7 || head == 0xf8 || head == 0xf9) {
					size = 3 + NumberUtil.toLong(vpack, start + 1, 2);
				} else if (head == 0xfa || head == 0xfb || head == 0xfc) {
					size = 5 + NumberUtil.toLong(vpack, start + 1, 4);
				} else /* if (head == 0xfd || head == 0xfe || head == 0xff) */ {
					size = 9 + NumberUtil.toLong(vpack, start + 1, 8);
				}
				break;
			default:
				throw new InternalError();
			}
		}
		return size;
	}

	/**
	 * @return array value at the specified index
	 * @throws VPackValueTypeException
	 */
	public VPackSlice at(final int index) {
		if (!isArray()) {
			throw new VPackValueTypeException(ValueType.Array);
		}
		return getNth(index);
	}

	public VPackSlice get(final String attribute) {
		if (!isObject()) {
			throw new VPackValueTypeException(ValueType.Object);
		}
		final byte head = head();
		VPackSlice result = new VPackSlice();
		if (head == 0x0a) {
			// special case, empty object
			result = new VPackSlice();
		} else if (head == 0x14) {
			// compact Object
			result = getFromCompactObject(attribute);
		} else {
			final int offsetsize = ObjectArrayUtil.getOffsetSize(head);
			final long end = NumberUtil.toLongReversed(vpack, start + 1, offsetsize);
			final long n;
			if (offsetsize < 8) {
				n = NumberUtil.toLongReversed(vpack, start + 1 + offsetsize, offsetsize);
			} else {
				n = NumberUtil.toLongReversed(vpack, (int) (start + end - offsetsize), offsetsize);
			}
			if (n == 1) {
				// Just one attribute, there is no index table!
				final VPackSlice key = new VPackSlice(vpack, start + findDataOffset());

				if (key.isString() && key.isEqualString(attribute)) {
					result = new VPackSlice(vpack, (int) (key.start + key.getByteSize()));
				}
			} else {
				final long ieBase = end - n * offsetsize - (offsetsize == 8 ? 8 : 0);

				// only use binary search for attributes if we have at least
				// this many entries
				// otherwise we'll always use the linear search
				final long sortedSearchEntriesThreshold = 4;

				final boolean sorted = head >= 0x0b && head <= 0x0e;
				if (sorted && n >= sortedSearchEntriesThreshold) {
					// This means, we have to handle the special case n == 1
					// only in the linear search!
					result = searchObjectKeyBinary(attribute, ieBase, offsetsize, n);
				} else {
					result = searchObjectKeyLinear(attribute, ieBase, offsetsize, n);
				}
			}
		}
		return result;
	}

	private VPackSlice getFromCompactObject(final String attribute) {
		VPackSlice result = new VPackSlice();
		for (final Iterator<VPackSlice> iterator = iterator(); iterator.hasNext();) {
			final VPackSlice key = iterator.next();
			if (key.isEqualString(attribute)) {
				result = new VPackSlice(vpack, (int) (key.start + key.getByteSize()));
				break;
			}
		}
		return result;
	}

	private VPackSlice searchObjectKeyBinary(
		final String attribute,
		final long ieBase,
		final int offsetsize,
		final long n) throws VPackValueTypeException {
		VPackSlice result;
		long l = 0;
		long r = n - 1;

		for (;;) {
			// midpoint
			final long index = l + ((r - l) / 2);
			final long offset = ieBase + index * offsetsize;
			final VPackSlice key = new VPackSlice(vpack,
					(int) (start + NumberUtil.toLong(vpack, (int) (start + offset), offsetsize)));
			int res = 0;
			if (key.isString()) {
				res = key.compareString(attribute);
			} else {
				// invalid key
				result = new VPackSlice();
				break;
			}
			if (res == 0) {
				// found
				result = new VPackSlice(vpack, (int) (key.start + key.getByteSize()));
				break;
			}
			if (res > 0) {
				if (index == 0) {
					result = new VPackSlice();
					break;
				}
				r = index - 1;
			} else {
				l = index + 1;
			}
			if (r < l) {
				result = new VPackSlice();
				break;
			}
		}
		return result;
	}

	private VPackSlice searchObjectKeyLinear(
		final String attribute,
		final long ieBase,
		final int offsetsize,
		final long n) throws VPackValueTypeException {
		VPackSlice result = new VPackSlice();
		for (long index = 0; index < n; index++) {
			final long offset = ieBase + index * offsetsize;
			final VPackSlice key = new VPackSlice(vpack,
					(int) (start + NumberUtil.toLong(vpack, (int) (start + offset), offsetsize)));
			if (key.isString()) {
				if (!key.isEqualString(attribute)) {
					continue;
				}
			} else {
				// invalid key type
				result = new VPackSlice();
				break;
			}
			// key is identical. now return value
			result = new VPackSlice(vpack, (int) (key.start + key.getByteSize()));
			break;
		}
		return result;

	}

	public VPackSlice keyAt(final int index) {
		if (!isObject()) {
			throw new VPackValueTypeException(ValueType.Object);
		}
		return getNthKey(index);
	}

	public VPackSlice valueAt(final int index) {
		if (!isObject()) {
			throw new VPackValueTypeException(ValueType.Object);
		}
		final VPackSlice key = getNthKey(index);
		return new VPackSlice(vpack, (int) (key.start + key.getByteSize()));
	}

	private VPackSlice getNthKey(final int index) {
		final VPackSlice slice = new VPackSlice(vpack, start + getNthOffset(index));
		return slice;
	}

	private VPackSlice getNth(final int index) {
		return new VPackSlice(vpack, start + getNthOffset(index));
	}

	/**
	 * 
	 * @return the offset for the nth member from an Array or Object type
	 */
	private int getNthOffset(final int index) {
		final int offset;
		final byte head = head();
		if (head == 0x13 || head == 0x14) {
			// compact Array or Object
			offset = getNthOffsetFromCompact(index);
		} else if (head == 0x01 || head == 0x0a) {
			// special case: empty Array or empty Object
			throw new IndexOutOfBoundsException();
		} else {
			final long n;
			final int offsetsize = ObjectArrayUtil.getOffsetSize(head);
			final long end = NumberUtil.toLongReversed(vpack, start + 1, offsetsize);
			int dataOffset = findDataOffset();
			if (head <= 0x05) {
				// array with no offset table or length
				final VPackSlice first = new VPackSlice(vpack, start + dataOffset);
				n = (end - dataOffset) / first.getByteSize();
			} else if (offsetsize < 8) {
				n = NumberUtil.toLongReversed(vpack, start + 1 + offsetsize, offsetsize);
			} else {
				n = NumberUtil.toLongReversed(vpack, (int) (start + end - offsetsize), offsetsize);
			}
			if (index >= n) {
				throw new IndexOutOfBoundsException();
			}
			if (head <= 0x05 || n == 1) {
				// no index table, but all array items have the same length
				// or only one item is in the array
				// now fetch first item and determine its length
				if (dataOffset == 0) {
					dataOffset = findDataOffset();
				}
				offset = (int) (dataOffset + index * new VPackSlice(vpack, start + dataOffset).getByteSize());
			} else {
				final long ieBase = end - n * offsetsize + index * offsetsize - (offsetsize == 8 ? 8 : 0);
				offset = (int) NumberUtil.toLongReversed(vpack, (int) (start + ieBase), offsetsize);
			}
		}
		return offset;
	}

	/**
	 * @return the offset for the nth member from a compact Array or Object type
	 */
	private int getNthOffsetFromCompact(final int index) {
		final long end = NumberUtil.readVariableValueLength(vpack, start + 1, false);
		final long n = NumberUtil.readVariableValueLength(vpack, (int) (start + end - 1), true);
		if (index >= n) {
			throw new IndexOutOfBoundsException();
		}
		final byte head = head();
		long offset = 1 + NumberUtil.getVariableValueLength(end);
		long current = 0;
		while (current != index) {
			final long byteSize = new VPackSlice(vpack, (int) (start + offset)).getByteSize();
			offset += byteSize;
			if (head == 0x14) {
				offset += byteSize;
			}
			++current;
		}
		return (int) offset;
	}

	private boolean isEqualString(final String s) {
		final String string = getAsString();
		return string.equals(s);
	}

	private int compareString(final String s) {
		final String string = getAsString();
		return string.compareTo(s);
	}

	public Iterator<VPackSlice> iterator() {
		final Iterator<VPackSlice> iterator;
		if (isObject()) {
			iterator = new ObjectIterator(this);
		} else if (isArray()) {
			iterator = new ArrayIterator(this);
		} else {
			throw new VPackValueTypeException(ValueType.Array, ValueType.Object);
		}
		return iterator;
	}
}
