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

	public Builder() {
		super();
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
		for (int i = 0; i < 8; i++) {
			// Will be filled later with bytelength and nr subs
			buffer.add((byte) 0x00);
		}
	}

	private void appendLength(final long length) {
		NumberUtil.appendReversed(buffer, length, 8);
	}

	private void reportAdd() {
		final Collection<Integer> depth = index.get(stack.size() - 1);
		depth.add(buffer.size() - stack.size() - 1);
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
		if (in.isEmpty()) {
			// empty Array or Object
			final Integer bufIndex = stack.get(stack.size() - 1);
			buffer.set(bufIndex, (byte) (isArray ? 0x01 : 0x0a));
			for (int i = bufIndex + 1; i < bufIndex + 1 + 8; i++) {
				buffer.remove(bufIndex + 1);// delete bytelength and nr
			}
			stack.remove(stack.size() - 1);
		} else if (false) {
			// check if we can use the compact Array / Object format
		} else {

		}
		// TODO
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
}
