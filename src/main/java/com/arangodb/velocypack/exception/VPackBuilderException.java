package com.arangodb.velocypack.exception;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public abstract class VPackBuilderException extends VPackException {

	protected VPackBuilderException() {
		super();
	}

	protected VPackBuilderException(final String message) {
		super(message);
	}

}
