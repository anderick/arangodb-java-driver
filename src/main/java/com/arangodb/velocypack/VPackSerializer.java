package com.arangodb.velocypack;

import com.arangodb.velocypack.exception.VPackBuilderException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public interface VPackSerializer<T> {

	void serialize(VPackBuilder builder, T entity) throws VPackBuilderException;

}
