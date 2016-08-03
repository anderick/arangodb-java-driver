package com.arangodb.model;

/**
 * @author Mark - mark at arangodb.com
 *
 */
@FunctionalInterface
public interface Callback<T> {

	void onComplete(T result);

}