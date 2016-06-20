package com.arangodb.velocypack.exception;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public abstract class VPackBuilderException extends Exception {

	protected VPackBuilderException() {
		super();
	}

	protected VPackBuilderException(final String message, final Throwable cause, final boolean enableSuppression,
		final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	protected VPackBuilderException(final String message, final Throwable cause) {
		super(message, cause);
	}

	protected VPackBuilderException(final String message) {
		super(message);
	}

	protected VPackBuilderException(final Throwable cause) {
		super(cause);
	}

}
