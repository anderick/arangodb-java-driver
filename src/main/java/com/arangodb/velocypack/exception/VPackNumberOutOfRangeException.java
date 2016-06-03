package com.arangodb.velocypack.exception;

import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackNumberOutOfRangeException extends VPackException {

	public VPackNumberOutOfRangeException(final ValueType type) {
		super(String.format("Number out of range of %s.%s", type.getClass().getSimpleName(), type.name()));
	}

}
