package com.arangodb.velocypack;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public interface VPackDeserializer<T> {

	T deserialize(VPackSlice vpack);

}
