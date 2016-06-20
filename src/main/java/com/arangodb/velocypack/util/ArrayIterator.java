package com.arangodb.velocypack.util;

import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackValueTypeException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class ArrayIterator extends SliceIterator {

	/**
	 * @param slice
	 * @throws VPackValueTypeException
	 */
	public ArrayIterator(final VPackSlice slice) throws VPackValueTypeException {
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
	public VPackSlice next() {
		try {
			return slice.at((int) position++);
		} catch (final VPackValueTypeException e) {
			throw new RuntimeException(e);
		}
	}

}
