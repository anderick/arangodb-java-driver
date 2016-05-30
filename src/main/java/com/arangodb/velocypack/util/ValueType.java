package com.arangodb.velocypack.util;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public enum ValueType {
	None, // not yet initialized
	Illegal, // illegal value
	Null, // JSON null
	Bool,
	Array,
	Object,
	Double,
	UTCDate,
	External,
	MinKey,
	MaxKey,
	Int,
	UInt,
	SmallInt,
	String,
	Binary,
	BCD,
	Custom
}
