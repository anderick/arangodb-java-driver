package com.arangodb.velocypack;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public interface VPackKeyTranslator {

	/**
	 * @param attribute
	 * @return the Integer representation of the attribute, if no adaption for
	 *         attribute exists return null
	 */
	Integer toKey(String attribute);

	/**
	 * 
	 * @param key
	 * @return the attribute of the Integer represenation, if no adaption for
	 *         key exists return null
	 */
	String fromKey(int key);

	void register(String attribute, int key);

}
