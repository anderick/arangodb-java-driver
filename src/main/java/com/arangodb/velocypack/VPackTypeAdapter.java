package com.arangodb.velocypack;

import com.arangodb.velocypack.exception.VPackBuilderException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public interface VPackTypeAdapter<T> {

	T toEntity(VPackSlice vpack);

	void fromEntity(VPackBuilder builder, T entity) throws VPackBuilderException;

}
