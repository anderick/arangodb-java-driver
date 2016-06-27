package com.arangodb.velocypack;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.arangodb.velocypack.util.NumberUtilTest;
import com.arangodb.velocypack.util.ValueTest;

/**
 * @author Mark - mark@arangodb.com
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ VPackBuilderTest.class, VPackSliceTest.class, NumberUtilTest.class, ValueTest.class,
		VPackSerializeDeserializeTest.class })
public class VPackTestSuite {

}
