package com.arangodb.model;

import java.util.Optional;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class DocumentUpdateOptions {

	private Optional<String> rev;
	private Optional<Boolean> waitForSync;
	private Optional<Boolean> keepNull;

	public Optional<String> getRev() {
		return rev;
	}

	public DocumentUpdateOptions setRev(final String rev) {
		this.rev = Optional.of(rev);
		return this;
	}

	public Optional<Boolean> getWaitForSync() {
		return waitForSync;
	}

	public DocumentUpdateOptions setWaitForSync(final Boolean waitForSync) {
		this.waitForSync = Optional.of(waitForSync);
		return this;
	}

	public Optional<Boolean> getKeepNull() {
		return keepNull;
	}

	public DocumentUpdateOptions setKeepNull(final Boolean keepNull) {
		this.keepNull = Optional.of(keepNull);
		return this;
	}

}
