package com.arangodb.velocypack.exception;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public abstract class VPackException extends Exception {

	protected VPackException() {
		super();
	}

	protected VPackException(final String message, final Throwable cause, final boolean enableSuppression,
		final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	protected VPackException(final String message, final Throwable cause) {
		super(message, cause);
	}

	protected VPackException(final String message) {
		super(message);
	}

	protected VPackException(final Throwable cause) {
		super(cause);
	}

}
