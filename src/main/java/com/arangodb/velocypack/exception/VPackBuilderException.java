package com.arangodb.velocypack.exception;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public abstract class VPackBuilderException extends Exception {

	public VPackBuilderException() {
		super();
	}

	public VPackBuilderException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public VPackBuilderException(final String message) {
		super(message);
	}

	public VPackBuilderException(final Throwable cause) {
		super(cause);
	}

}
