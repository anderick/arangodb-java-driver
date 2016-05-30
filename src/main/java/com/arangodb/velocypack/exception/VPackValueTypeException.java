package com.arangodb.velocypack.exception;

import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackValueTypeException extends RuntimeException {

	public VPackValueTypeException(final ValueType type) {
		super(String.format("Expecting type %s", type));
	}

}
