package com.arangodb.velocypack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.arangodb.velocypack.exception.VPackBuilderKeyAlreadyWrittenException;
import com.arangodb.velocypack.exception.VPackBuilderNeedOpenCompoundException;
import com.arangodb.velocypack.exception.VPackBuilderNeedOpenObjectException;
import com.arangodb.velocypack.exception.VPackBuilderUnexpectedValueException;
import com.arangodb.velocypack.exception.VPackNumberOutOfRangeException;
import com.arangodb.velocypack.util.DateUtil;
import com.arangodb.velocypack.util.NumberUtil;
import com.arangodb.velocypack.util.StringUtil;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class Builder {

	private final ArrayList<Byte> buffer; // Here we collect the result
	private final ArrayList<Integer> stack; // Start positions of open
											// objects/arrays
	private final Map<Integer, ArrayList<Integer>> index; // Indices for starts
															// of
															// subindex
	private boolean keyWritten; // indicates that in the current object the key
								// has been written but the value not yet
	private final BuilderOptions options;

	public Builder() {
		this(new BuilderOptions());
	}

	public Builder(final BuilderOptions options) {
		super();
		this.options = options;
		buffer = new ArrayList<Byte>();
		stack = new ArrayList<Integer>();
		index = new HashMap<Integer, ArrayList<Integer>>();
	}

	public Builder add(final Value sub) throws VPackBuilderUnexpectedValueException, VPackNumberOutOfRangeException {
		addInternal(sub);
		return this;
	}

	public Builder add(final String attribute, final Value sub)
			throws VPackBuilderNeedOpenObjectException, VPackBuilderKeyAlreadyWrittenException,
			VPackBuilderUnexpectedValueException, VPackNumberOutOfRangeException {
		addInternal(attribute, sub);
		return this;
	}

	private void addInternal(final Value sub)
			throws VPackBuilderUnexpectedValueException, VPackNumberOutOfRangeException {
		boolean haveReported = false;
		if (!stack.isEmpty()) {
			if (!keyWritten) {
				reportAdd();
				haveReported = true;
			}
		}
		try {
			set(sub);
		} catch (final VPackBuilderUnexpectedValueException e) {
			// clean up in case of an exception
			if (haveReported) {
				cleanupAdd();
			}
			throw e;
		} catch (final VPackNumberOutOfRangeException e) {
			// clean up in case of an exception
			if (haveReported) {
				cleanupAdd();
			}
			throw e;
		}
	}

	private void addInternal(final String attribute, final Value sub)
			throws VPackBuilderNeedOpenObjectException, VPackBuilderKeyAlreadyWrittenException,
			VPackBuilderUnexpectedValueException, VPackNumberOutOfRangeException {
		boolean haveReported = false;
		if (!stack.isEmpty()) {
			final byte head = head();
			if (head != 0x0b && head != 0x14) {
				throw new VPackBuilderNeedOpenObjectException();
			}
			if (keyWritten) {
				throw new VPackBuilderKeyAlreadyWrittenException();
			}
			reportAdd();
			haveReported = true;
		}
		try {
			set(new Value(attribute));
			keyWritten = true;
			set(sub);
		} catch (final VPackBuilderUnexpectedValueException e) {
			// clean up in case of an exception
			if (haveReported) {
				cleanupAdd();
			}
			throw e;
		} catch (final VPackNumberOutOfRangeException e) {
			// clean up in case of an exception
			if (haveReported) {
				cleanupAdd();
			}
			throw e;
		} finally {
			keyWritten = false;
		}
	}

	private void set(final Value item) throws VPackBuilderUnexpectedValueException, VPackNumberOutOfRangeException {
		final Class<?> clazz = item.getClazz();
		switch (item.getType()) {
		case Null:
			appendNull();
			break;
		case Bool:
			checkClass(clazz, ValueType.Bool, Boolean.class);
			appendBoolean(item.getBoolean());
			break;
		case Double:
			checkClass(clazz, ValueType.Double, Double.class);
			appendDouble(item.getDouble());
			break;
		case SmallInt:
			final long vSmallInt;
			if (clazz == Long.class) {
				vSmallInt = item.getLong();
			} else if (clazz == Integer.class) {
				vSmallInt = item.getInteger();
			} else if (clazz == BigInteger.class) {
				vSmallInt = item.getBigInteger().longValue();
			} else {
				throw new VPackBuilderUnexpectedValueException(ValueType.SmallInt, Long.class, Integer.class,
						BigInteger.class);
			}
			if (vSmallInt < -6 || vSmallInt > 9) {
				throw new VPackNumberOutOfRangeException(ValueType.SmallInt);
			}
			appendSmallInt(vSmallInt);
			break;
		case Int:
			final long vInt;
			if (clazz == Long.class) {
				vInt = item.getLong();
			} else if (clazz == Integer.class) {
				vInt = item.getInteger();
			} else if (clazz == BigInteger.class) {
				vInt = item.getBigInteger().longValue();
			} else {
				throw new VPackBuilderUnexpectedValueException(ValueType.Int, Long.class, Integer.class,
						BigInteger.class);
			}
			appendInt(vInt);
			break;
		case UInt:
			BigInteger vUInt;
			if (clazz == Long.class) {
				vUInt = BigInteger.valueOf(item.getLong());
			} else if (clazz == Integer.class) {
				vUInt = BigInteger.valueOf(item.getInteger());
			} else if (clazz == BigInteger.class) {
				vUInt = item.getBigInteger();
			} else {
				throw new VPackBuilderUnexpectedValueException(ValueType.UInt, Long.class, Integer.class,
						BigInteger.class);
			}
			if (vUInt.compareTo(BigInteger.ZERO) == -1) {
				throw new VPackBuilderUnexpectedValueException(ValueType.UInt, "non-negative", Long.class,
						Integer.class, BigInteger.class);
			}
			appendUInt(vUInt);
			break;
		case UTCDate:
			checkClass(clazz, ValueType.UTCDate, Date.class);
			appendUTCDate(item.getDate());
			break;
		case String:
			checkClass(clazz, ValueType.String, String.class);
			appendString(item.getString());
			break;
		case Array:
			addArray(item.isUnindexed());
			break;
		case Object:
			addObject(item.isUnindexed());
			break;
		default:
			break;
		}
	}

	private void checkClass(final Class<?> clazz, final ValueType type, final Class<?> expectedClass)
			throws VPackBuilderUnexpectedValueException {
		if (expectedClass != clazz) {
			throw new VPackBuilderUnexpectedValueException(type, clazz);
		}
	}

	private void appendNull() {
		buffer.add((byte) 0x18);
	}

	private void appendBoolean(final boolean value) {
		if (value) {
			buffer.add((byte) 0x1a);
		} else {
			buffer.add((byte) 0x19);
		}
	}

	private void appendDouble(final double value) {
		buffer.add((byte) 0x1b);
		NumberUtil.append(buffer, value);
	}

	private void appendSmallInt(final long value) {
		if (value >= 0) {
			buffer.add((byte) (value + 0x30));
		} else {
			buffer.add((byte) (value + 0x40));
		}
	}

	private void appendInt(final long value) {
		buffer.add((byte) 0x27);
		NumberUtil.append(buffer, value, Long.BYTES);
	}

	private void appendUInt(final BigInteger value) {
		buffer.add((byte) 0x2f);
		NumberUtil.append(buffer, value, Long.BYTES);
	}

	private void appendUTCDate(final Date value) {
		buffer.add((byte) 0x1c);
		DateUtil.append(buffer, value);
	}

	private void appendString(final String value) {
		final int length = value.length();
		if (length <= 126) {
			// short string
			buffer.add((byte) (0x40 + length));
		} else {
			// long string
			buffer.add((byte) 0xbf);
			appendLength(length);
		}
		StringUtil.append(buffer, value);
	}

	private void addArray(final boolean unindexed) {
		addCompoundValue((byte) (unindexed ? 0x13 : 0x06));
	}

	private void addObject(final boolean unindexed) {
		addCompoundValue((byte) (unindexed ? 0x14 : 0x0b));
	}

	private void addCompoundValue(final byte head) {
		// an Array or Object is started:
		stack.add(buffer.size());
		index.put(stack.size() - 1, new ArrayList<Integer>());
		buffer.add(head);
	}

	private void appendLength(final long length) {
		NumberUtil.appendReversed(buffer, length, 8);
	}

	private void reportAdd() {
		final Collection<Integer> depth = index.get(stack.size() - 1);
		depth.add(buffer.size() - stack.get(stack.size() - 1) + 1);
	}

	private void cleanupAdd() {
		final ArrayList<Integer> depth = index.get(stack.size() - 1);
		depth.remove(depth.size() - 1);
	}

	public Builder close() throws VPackBuilderNeedOpenCompoundException {
		if (isClosed()) {
			throw new VPackBuilderNeedOpenCompoundException();
		}
		final byte head = head();
		final boolean isArray = (head == 0x06 || head == 0x13);
		final ArrayList<Integer> in = index.get(stack.size() - 1);
		final int tos = stack.get(stack.size() - 1);
		if (in.isEmpty()) {
			// empty Array or Object
			buffer.set(tos, (byte) (isArray ? 0x01 : 0x0a));
			stack.remove(stack.size() - 1);
			return this;
		} else if (in.size() > 1 && (head == 0x13 || head == 0x14 || (head == 0x06 && options.isBuildUnindexedArrays())
				|| head == 0x0b && options.isBuildUnindexedObjects())) {
			// use the compact Array / Object format
			final long nLen = NumberUtil.getVariableValueLength(in.size());
			long byteSize = buffer.size() - tos + nLen;
			long bLen = NumberUtil.getVariableValueLength(byteSize);
			byteSize += bLen;
			if (NumberUtil.getVariableValueLength(byteSize) != bLen) {
				byteSize += 1;
				bLen += 1;
			}
			if (bLen < 9) {
				// can only use compact notation if total byte length is at most
				// 8 bytes long
				buffer.set(tos, (byte) (isArray ? 0x13 : 0x14));
				// store byte length
				NumberUtil.storeVariableValueLength(buffer, tos + 1, byteSize, false);
				// store number of values
				NumberUtil.storeVariableValueLength(buffer, (int) (tos + byteSize - 1), in.size(), true);
				stack.remove(stack.size() - 1);
				return this;
			}
		}
		// fix head byte in case a compact Array / Object was originally
		// requested
		buffer.set(tos, (byte) (isArray ? 0x06 : 0x0b));

		boolean needIndexTable = true;
		boolean needNrSubs = true;
		if (in.size() == 1) {
			needIndexTable = false;
			if (isArray) {
				// For objects we leave needNrSubs at true here!
				needNrSubs = false;
			}
		}
		// First determine byte length and its format:
		final int offsetSize;
		// can be 1, 2, 4 or 8 for the byte width of the offsets,
		// the byte length and the number of subvalues:
		if ((buffer.size() - 1 - tos) + (needIndexTable ? in.size() : 0) - (needNrSubs ? 6 : 7) <= 0xff) {
			// We have so far used _pos - tos bytes, including the reserved 8
			// bytes for byte length and number of subvalues. In the 1-byte
			// number
			// case we would win back 6 bytes but would need one byte per
			// subvalue
			// for the index table
			offsetSize = 1;
		} else if ((buffer.size() - 1 - tos) + (needIndexTable ? 2 * in.size() : 0) <= 0xffff) {
			offsetSize = 2;
		} else if (((buffer.size() - 1 - tos) / 2)
				+ ((needIndexTable ? 4 * in.size() : 0) / 2) <= Integer.MAX_VALUE/* 0xffffffffu */) {
			offsetSize = 4;
		} else {
			offsetSize = 8;
		}
		// Now build the table:
		if (needIndexTable) {
			if (buffer.get(tos) == 0x0b) {
				// Object
				buffer.set(tos, (byte) 0x0f); // unsorted
			}
			for (int i = 0; i < in.size(); i++) {
				NumberUtil.append(buffer, in.get(i) + offsetSize, offsetSize);
			}
		} else { // no index table
			if (buffer.get(tos) == 0x06) {
				buffer.set(tos, (byte) 0x02);
			}
		}
		// Finally fix the byte width in the type byte:
		if (offsetSize > 1) {
			if (offsetSize == 2) {
				buffer.set(tos, (byte) (buffer.get(tos) + 1));
			} else if (offsetSize == 4) {
				buffer.set(tos, (byte) (buffer.get(tos) + 2));
			} else { // offsetSize == 8
				buffer.set(tos, (byte) (buffer.get(tos) + 3));
				if (needNrSubs) {
					appendLength(in.size());
				}
			}
		}
		if (offsetSize < 8 && needNrSubs) {
			int x = in.size();
			for (int i = 1; i < 2 * offsetSize; i++) {
				buffer.add(tos + i, (byte) (x & 0xff));
				x >>= 8;
			}
		}
		// Fix the byte length in the beginning:
		long x = buffer.size() - tos + offsetSize;
		for (int i = 1; i <= offsetSize; i++) {
			buffer.add(tos + i, (byte) (x & 0xff));
			x >>= 8;
		}
		stack.remove(stack.size() - 1);
		return this;
	}

	private boolean isClosed() {
		return stack.isEmpty();
	}

	/**
	 * 
	 * @return head of open object/array
	 */
	private byte head() {
		final Integer index = stack.get(stack.size() - 1);
		final byte header = buffer.get(index);
		return header;
	}

	public Slice slice() {
		final Slice slice;
		if (buffer.isEmpty()) {
			slice = new Slice();
		} else {
			// TODO find a way without iterate and copy
			final byte[] vpack = new byte[buffer.size()];
			int i = 0;
			for (final byte b : buffer) {
				vpack[i++] = b;
			}
			slice = new Slice(vpack);
		}
		return slice;
	}

	public static class BuilderOptions {
		private boolean buildUnindexedArrays;
		private boolean buildUnindexedObjects;

		public BuilderOptions() {
			super();
			buildUnindexedArrays = false;
			buildUnindexedObjects = false;
		}

		public boolean isBuildUnindexedArrays() {
			return buildUnindexedArrays;
		}

		public void setBuildUnindexedArrays(final boolean buildUnindexedArrays) {
			this.buildUnindexedArrays = buildUnindexedArrays;
		}

		public boolean isBuildUnindexedObjects() {
			return buildUnindexedObjects;
		}

		public void setBuildUnindexedObjects(final boolean buildUnindexedObjects) {
			this.buildUnindexedObjects = buildUnindexedObjects;
		}
	}
}
