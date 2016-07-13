package com.arangodb.velocypack.util;

import java.util.NoSuchElementException;

import com.arangodb.velocypack.VPackSlice;
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
	public ObjectIterator(final VPackSlice slice) throws VPackValueTypeException {
		super(slice);
		if (!slice.isObject()) {
			throw new VPackValueTypeException(ValueType.OBJECT);
		}
		if (size > 0) {
			current = slice.keyAt(0).getStart();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public VPackSlice next() {
		if (position++ > 0) {
			if (position <= size && current != 0) {
				// skip over key
				current += getCurrent().getByteSize();
				// skip over value
				current += getCurrent().getByteSize();
			} else {
				throw new NoSuchElementException();
			}
		}
		return getCurrent();
	}

}
