package com.arangodb.velocypack.util;

import com.arangodb.velocypack.Slice;
import com.arangodb.velocypack.exception.VPackValueTypeException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class ArrayIterator extends SliceIterator {

	/**
	 * @param slice
	 */
	public ArrayIterator(final Slice slice) {
		super(slice);
		if (!slice.isArray()) {
			throw new VPackValueTypeException(ValueType.Array);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Slice next() {
		return slice.at((int) position++);
	}

}
