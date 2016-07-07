package com.arangodb.velocypack;

import com.arangodb.velocypack.exception.VPackParserException;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public interface VPackSerializationContext {

	void serialize(VPackBuilder builder, final Object entity) throws VPackParserException;

	void serialize(VPackBuilder builder, final String attribute, final Object entity) throws VPackParserException;

}
