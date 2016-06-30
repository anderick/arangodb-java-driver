package com.arangodb.velocypack;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arangodb.velocypack.VPackSlice.SliceOptions;
import com.arangodb.velocypack.defaults.VPackDefaultOptions;
import com.arangodb.velocypack.exception.VPackBuilderException;
import com.arangodb.velocypack.exception.VPackBuilderKeyAlreadyWrittenException;
import com.arangodb.velocypack.exception.VPackBuilderNeedOpenCompoundException;
import com.arangodb.velocypack.exception.VPackBuilderNeedOpenObjectException;
import com.arangodb.velocypack.exception.VPackBuilderNumberOutOfRangeException;
import com.arangodb.velocypack.exception.VPackBuilderUnexpectedValueException;
import com.arangodb.velocypack.util.DateUtil;
import com.arangodb.velocypack.util.NumberUtil;
import com.arangodb.velocypack.util.StringUtil;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackBuilder {

	public static interface BuilderOptions extends SliceOptions {
		boolean isBuildUnindexedArrays();

		void setBuildUnindexedArrays(boolean buildUnindexedArrays);

		boolean isBuildUnindexedObjects();

		void setBuildUnindexedObjects(boolean buildUnindexedObjects);
	}

	private static final int LONG_BYTES = 8;
	private static final int INT_BYTES = 4;
	private static final int SHORT_BYTES = 2;

	private final ArrayList<Byte> buffer; // Here we collect the result
	private final ArrayList<Integer> stack; // Start positions of open
											// objects/arrays
	private final Map<Integer, List<Integer>> index; // Indices for starts
														// of
														// subindex
	private boolean keyWritten; // indicates that in the current object the key
								// has been written but the value not yet
	private final BuilderOptions options;

	public VPackBuilder() {
		this(new VPackDefaultOptions());
	}

	public VPackBuilder(final BuilderOptions options) {
		super();
		this.options = options;
		buffer = new ArrayList<Byte>();
		stack = new ArrayList<Integer>();
		index = new HashMap<Integer, List<Integer>>();
	}

	public BuilderOptions getOptions() {
		return options;
	}

	public VPackBuilder add(final Value sub) throws VPackBuilderException {
		addInternal(sub);
		return this;
	}

	public VPackBuilder add(final String attribute, final Value sub) throws VPackBuilderException {
		addInternal(attribute, sub);
		return this;
	}

	private void addInternal(final Value sub) throws VPackBuilderException {
		boolean haveReported = false;
		if (!stack.isEmpty() && !keyWritten) {
			reportAdd();
			haveReported = true;
		}
		try {
			set(sub);
		} catch (final VPackBuilderException e) {
			// clean up in case of an exception
			if (haveReported) {
				cleanupAdd();
			}
			throw e;
		}
	}

	private void addInternal(final String attribute, final Value sub) throws VPackBuilderException {
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
			final VPackKeyTranslator keyTranslator = options.getKeyTranslator();
			final Integer key = keyTranslator != null ? keyTranslator.toKey(attribute) : null;
			final Value attr = key != null ? new Value(key, (key < -6 || key > 9) ? ValueType.INT : ValueType.SMALLINT)
					: new Value(attribute);
			set(attr);
			keyWritten = true;
			set(sub);
		} catch (final VPackBuilderException e) {
			// clean up in case of an exception
			if (haveReported) {
				cleanupAdd();
			}
			throw e;
		} finally {
			keyWritten = false;
		}
	}

	private void set(final Value item) throws VPackBuilderException {
		final Class<?> clazz = item.getClazz();
		switch (item.getType()) {
		case NULL:
			appendNull();
			break;
		case BOOL:
			checkClass(clazz, ValueType.BOOL, Boolean.class);
			appendBoolean(item.getBoolean());
			break;
		case DOUBLE:
			final double d;
			if (clazz == Double.class) {
				d = item.getDouble();
			} else if (clazz == BigDecimal.class) {
				d = item.getBigDecimal().doubleValue();
			} else if (clazz == Float.class) {
				d = item.getFloat().doubleValue();
			} else {
				throw new VPackBuilderUnexpectedValueException(ValueType.DOUBLE, Double.class, BigDecimal.class,
						Float.class);
			}
			appendDouble(d);
			break;
		case SMALLINT:
			final long vSmallInt;
			if (clazz == Long.class) {
				vSmallInt = item.getLong();
			} else if (clazz == Integer.class) {
				vSmallInt = item.getInteger();
			} else if (clazz == BigInteger.class) {
				vSmallInt = item.getBigInteger().longValue();
			} else {
				throw new VPackBuilderUnexpectedValueException(ValueType.SMALLINT, Long.class, Integer.class,
						BigInteger.class);
			}
			if (vSmallInt < -6 || vSmallInt > 9) {
				throw new VPackBuilderNumberOutOfRangeException(ValueType.SMALLINT);
			}
			appendSmallInt(vSmallInt);
			break;
		case INT:
			if (clazz == Long.class) {
				appendLong(item.getLong());
			} else if (clazz == Integer.class) {
				appendInt(item.getInteger());
			} else if (clazz == BigInteger.class) {
				appendLong(item.getBigInteger().longValue());
			} else if (clazz == Short.class) {
				appendShort(item.getShort());
			} else {
				throw new VPackBuilderUnexpectedValueException(ValueType.INT, Long.class, Integer.class,
						BigInteger.class, Short.class);
			}
			break;
		case UINT:
			final BigInteger vUInt;
			if (clazz == Long.class) {
				vUInt = BigInteger.valueOf(item.getLong());
			} else if (clazz == Integer.class) {
				vUInt = BigInteger.valueOf(item.getInteger());
			} else if (clazz == BigInteger.class) {
				vUInt = item.getBigInteger();
			} else {
				throw new VPackBuilderUnexpectedValueException(ValueType.UINT, Long.class, Integer.class,
						BigInteger.class);
			}
			if (-1 == vUInt.compareTo(BigInteger.ZERO)) {
				throw new VPackBuilderUnexpectedValueException(ValueType.UINT, "non-negative", Long.class,
						Integer.class, BigInteger.class);
			}
			appendUInt(vUInt);
			break;
		case UTC_DATE:
			checkClass(clazz, ValueType.UTC_DATE, Date.class);
			appendUTCDate(item.getDate());
			break;
		case STRING:
			final String string;
			if (clazz == String.class) {
				string = item.getString();
			} else if (clazz == Character.class) {
				string = String.valueOf(item.getCharacter());
			} else {
				throw new VPackBuilderUnexpectedValueException(ValueType.STRING, String.class, Character.class);
			}
			appendString(string);
			break;
		case ARRAY:
			addArray(item.isUnindexed());
			break;
		case OBJECT:
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

	private void appendLong(final long value) {
		buffer.add((byte) 0x27);
		NumberUtil.append(buffer, value, LONG_BYTES);
	}

	private void appendInt(final int value) {
		buffer.add((byte) 0x23);
		NumberUtil.append(buffer, value, INT_BYTES);
	}

	private void appendShort(final short value) {
		buffer.add((byte) 0x21);
		NumberUtil.append(buffer, value, SHORT_BYTES);
	}

	private void appendUInt(final BigInteger value) {
		buffer.add((byte) 0x2f);
		NumberUtil.append(buffer, value, LONG_BYTES);
	}

	private void appendUTCDate(final Date value) {
		buffer.add((byte) 0x1c);
		DateUtil.append(buffer, value);
	}

	private void appendString(final String value) throws VPackBuilderException {
		final int length = value.length();
		if (length <= 126) {
			// short string
			buffer.add((byte) (0x40 + length));
		} else {
			// long string
			buffer.add((byte) 0xbf);
			appendLength(length);
		}
		try {
			StringUtil.append(buffer, value);
		} catch (final UnsupportedEncodingException e) {
			throw new VPackBuilderException(e);
		}
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
		final List<Integer> depth = index.get(stack.size() - 1);
		depth.remove(depth.size() - 1);
	}

	public VPackBuilder close() throws VPackBuilderNeedOpenCompoundException {
		return close(true);
	}

	protected VPackBuilder close(final boolean sort) throws VPackBuilderNeedOpenCompoundException {
		if (isClosed()) {
			throw new VPackBuilderNeedOpenCompoundException();
		}
		final byte head = head();
		final boolean isArray = head == 0x06 || head == 0x13;
		final List<Integer> in = index.get(stack.size() - 1);
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
			if (sort && buffer.get(tos) == 0x0b) {
				// Object
				sortObjectIndex(tos, in);
			}
			for (int i = 0; i < in.size(); i++) {
				final long value = in.get(i) + (offsetSize == 1 ? offsetSize : offsetSize + 1);
				NumberUtil.appendReversed(buffer, value, offsetSize);
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
		// set the number of items in the beginning
		if (offsetSize < 8 && needNrSubs) {
			int x = in.size();
			for (int i = 1; i <= offsetSize; i++) {
				buffer.add(tos + i, (byte) (x & 0xff));
				x >>= 8;
			}
		}
		// set the byte length in the beginning
		long x = buffer.size() - tos + offsetSize;
		for (int i = 1; i <= offsetSize; i++) {
			buffer.add(tos + i, (byte) (x & 0xff));
			x >>= 8;
		}
		stack.remove(stack.size() - 1);
		return this;
	}

	private void sortObjectIndex(final int start, final List<Integer> offsets) {
		final byte[] vpack = getVpack();
		final Comparator<Integer> c = new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				final VPackSlice key1 = new VPackSlice(vpack, start + o1 - 1, options);
				final String key1AsString = getKeyAsString(key1);
				final VPackSlice key2 = new VPackSlice(vpack, start + o2 - 1, options);
				final String key2AsString = getKeyAsString(key2);
				return key1AsString.compareTo(key2AsString);
			}
		};
		Collections.sort(offsets, c);
	}

	private String getKeyAsString(final VPackSlice key) {
		final String result;
		if (key.isString()) {
			result = key.getAsString();
		} else if (key.isInteger()) {
			result = options.getKeyTranslator().fromKey(key.getAsInt());
		} else {
			result = "";
		}
		return result;
	}

	private boolean isClosed() {
		return stack.isEmpty();
	}

	/**
	 * 
	 * @return head of open object/array
	 */
	private byte head() {
		final Integer in = stack.get(stack.size() - 1);
		return buffer.get(in);
	}

	public VPackSlice slice() {
		final VPackSlice slice;
		if (buffer.isEmpty()) {
			slice = new VPackSlice();
		} else {
			slice = new VPackSlice(getVpack(), options);
		}
		return slice;
	}

	private byte[] getVpack() {
		final byte[] vpack = new byte[buffer.size()];
		int i = 0;
		for (final byte b : buffer) {
			vpack[i++] = b;
		}
		return vpack;
	}
}
