package com.arangodb.velocypack;

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
public class Slice {

	private final byte[] vpack;
	private final int start;

	private Slice() {
		this(new byte[] { 0x00 }, 0);
	}

	public Slice(final byte[] vpack) {
		this(vpack, 0);
	}

	public Slice(final byte[] vpack, final int start) {
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

	public long getUInt() {
		if (!isUInt()) {
			throw new VPackValueTypeException(ValueType.UInt);
		}
		return NumberUtil.toLong(vpack, start + 1, length());
	}

	public BigInteger getUIntAsBigInteger() {
		if (!isUInt()) {
			throw new VPackValueTypeException(ValueType.UInt);
		}
		return NumberUtil.toBigInteger(vpack, start + 1, length());
	}

	private long getUIntUnchecked() {

		final long uint;
		final byte head = head();
		if (head >= 0x28 && head <= 0x2f) {
			// UInt
			uint = NumberUtil.toLong(vpack, start + 1, head - 0x27);
		} else if (head >= 0x30 && head <= 0x39) {
			// Smallint >= 0
			uint = head - 0x30;
		} else {
			uint = 0;
		}
		return uint;
	}

	public long getInteger() {
		final long result;
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
				// array with no offset table or length
				final int dataOffset = findDataOffset();
				final Slice first = new Slice(vpack, start + dataOffset);
				length = (end - dataOffset) / first.getByteSize();
			} else if (offsetsize < 8) {
				length = NumberUtil.toLongReversed(vpack, start + 1 + offsetsize, offsetsize);
			} else {
				length = NumberUtil.toLongReversed(vpack, (int) (start + end - offsetsize), offsetsize);
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
				// TODO
				throw new UnsupportedOperationException();
			case BCD:
				// TODO
				throw new UnsupportedOperationException();
			case Custom:
				// TODO
				throw new UnsupportedOperationException();
			default:
				throw new InternalError();
			}
		}
		return size;
	}

	/**
	 * @return array value at the specified index
	 */
	public Slice at(final int index) {
		if (!isArray()) {
			throw new VPackValueTypeException(ValueType.Array);
		}
		return getNth(index);
	}

	public Slice get(final String attribute) {
		if (!isObject()) {
			throw new VPackValueTypeException(ValueType.Object);
		}
		final byte head = head();
		Slice result = new Slice();
		if (head == 0x0a) {
			// special case, empty object
			result = new Slice();
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
				final Slice key = new Slice(vpack, start + findDataOffset());

				if (key.isString() && key.isEqualString(attribute)) {
					result = new Slice(vpack, (int) (key.start + key.getByteSize()));
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

	private Slice getFromCompactObject(final String attribute) {
		Slice result = new Slice();
		for (final Iterator<Slice> iterator = iterator(); iterator.hasNext();) {
			final Slice key = iterator.next();
			if (key.isEqualString(attribute)) {
				result = new Slice(vpack, (int) (key.start + key.getByteSize()));
				break;
			}
		}
		return result;
	}

	private Slice searchObjectKeyBinary(final String attribute, final long ieBase, final int offsetsize, final long n) {
		Slice result;
		long l = 0;
		long r = n - 1;

		for (;;) {
			// midpoint
			final long index = l + ((r - l) / 2);
			final long offset = ieBase + index * offsetsize;
			final Slice key = new Slice(vpack,
					(int) (start + NumberUtil.toLong(vpack, (int) (start + offset), offsetsize)));
			int res = 0;
			if (key.isString()) {
				res = key.compareString(attribute);
			} else {
				// invalid key
				result = new Slice();
				break;
			}
			if (res == 0) {
				// found
				result = new Slice(vpack, (int) (key.start + key.getByteSize()));
				break;
			}
			if (res > 0) {
				if (index == 0) {
					result = new Slice();
					break;
				}
				r = index - 1;
			} else {
				l = index + 1;
			}
			if (r < l) {
				result = new Slice();
				break;
			}
		}
		return result;
	}

	private Slice searchObjectKeyLinear(final String attribute, final long ieBase, final int offsetsize, final long n) {
		Slice result = new Slice();
		for (long index = 0; index < n; index++) {
			final long offset = ieBase + index * offsetsize;
			final Slice key = new Slice(vpack,
					(int) (start + NumberUtil.toLong(vpack, (int) (start + offset), offsetsize)));
			if (key.isString()) {
				if (!key.isEqualString(attribute)) {
					continue;
				}
			} else {
				// invalid key type
				result = new Slice();
				break;
			}
			// key is identical. now return value
			result = new Slice(vpack, (int) (key.start + key.getByteSize()));
			break;
		}
		return result;

	}

	public Slice keyAt(final int index) {
		if (!isObject()) {
			throw new VPackValueTypeException(ValueType.Object);
		}
		return getNthKey(index);
	}

	public Slice valueAt(final int index) {
		if (!isObject()) {
			throw new VPackValueTypeException(ValueType.Object);
		}
		final Slice key = getNthKey(index);
		return new Slice(vpack, (int) (key.start + key.getByteSize()));
	}

	private Slice getNthKey(final int index) {
		final Slice slice = new Slice(vpack, start + getNthOffset(index));
		return slice;
	}

	private Slice getNth(final int index) {
		return new Slice(vpack, start + getNthOffset(index));
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
				final Slice first = new Slice(vpack, start + dataOffset);
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
				offset = (int) (dataOffset + index * new Slice(vpack, start + dataOffset).getByteSize());
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
			final long byteSize = new Slice(vpack, (int) (start + offset)).getByteSize();
			offset += byteSize;
			if (head == 0x14) {
				offset += byteSize;
			}
			++current;
		}
		return (int) offset;
	}

	private boolean isEqualString(final String s) {
		final String string = getString();
		return string.equals(s);
	}

	private int compareString(final String s) {
		final String string = getString();
		return string.compareTo(s);
	}

	public Iterator<Slice> iterator() {
		final Iterator<Slice> iterator;
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
