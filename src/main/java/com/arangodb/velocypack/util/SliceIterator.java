package com.arangodb.velocypack.util;

import java.util.Iterator;

import com.arangodb.velocypack.Slice;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public abstract class SliceIterator implements Iterator<Slice> {

	protected final Slice slice;
	protected final long size;
	protected long position;
	protected long current;

	protected SliceIterator(final Slice slice) {
		super();
		this.slice = slice;
		size = slice.getLength();
		position = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return position < size;
	}

	protected Slice getCurrent() {
		return new Slice(slice.getVpack(), (int) current);
	}
}
