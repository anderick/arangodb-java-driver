package com.arangodb.velocypack;

import com.arangodb.velocypack.exception.VPackException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public interface VPackSerializer<T> {

	void serialize(VPackBuilder builder, T entity) throws VPackException;

}
