package com.arangodb.velocypack.exception;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public abstract class VPackException extends Exception {

	public VPackException() {
		super();
	}

	public VPackException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public VPackException(final String message) {
		super(message);
	}

	public VPackException(final Throwable cause) {
		super(cause);
	}

}
