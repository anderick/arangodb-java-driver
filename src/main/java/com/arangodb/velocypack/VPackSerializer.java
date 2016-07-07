package com.arangodb.velocypack;

import com.arangodb.velocypack.exception.VPackException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public interface VPackSerializer<T> {

	void serialize(VPackBuilder builder, String attribute, T entity, VPackSerializationContext context)
			throws VPackException;

}
