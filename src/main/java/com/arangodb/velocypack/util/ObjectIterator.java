package com.arangodb.velocypack.util;

import com.arangodb.velocypack.Slice;
import com.arangodb.velocypack.exception.VPackValueTypeException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class ObjectIterator extends SliceIterator {

	/**
	 * @param slice
	 * @throws VPackValueTypeException
	 */
	public ObjectIterator(final Slice slice) throws VPackValueTypeException {
		super(slice);
		if (!slice.isObject()) {
			throw new VPackValueTypeException(ValueType.Object);
		}
		if (size > 0) {
			final byte head = slice.head();
			if (head == 0x14) {
				current = slice.keyAt(0).getStart();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Slice next() {
		if (position++ > 0) {
			if (position <= size && current != 0) {
				// skip over key
				current += getCurrent().getByteSize();
				// skip over value
				current += getCurrent().getByteSize();
			} else {
				current = 0;
			}
		}
		return getCurrent();
	}

}
