package com.arangodb.velocypack.defaults;

import java.util.HashMap;
import java.util.Map;

import com.arangodb.velocypack.VPackKeyTranslator;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackDefaultKeyTranslator implements VPackKeyTranslator {

	private static final String KEY = "_key";
	private static final String REV = "_rev";
	private static final String ID = "_id";
	private static final String FROM = "_from";
	private static final String TO = "_to";

	private static final byte KEY_ATTRIBUTE = 0x31;
	private static final byte REV_ATTRIBUTE = 0x32;
	private static final byte ID_ATTRIBUTE = 0x33;
	private static final byte FROM_ATTRIBUTE = 0x34;
	private static final byte TO_ATTRIBUTE = 0x35;
	private static final byte ATTRIBUTE_BASE = 0x30;

	protected final Map<String, Integer> attributeToKey;
	protected final Map<Integer, String> keyToAttribute;

	public VPackDefaultKeyTranslator() {
		super();
		attributeToKey = new HashMap<String, Integer>();
		keyToAttribute = new HashMap<Integer, String>();
		register(KEY, KEY_ATTRIBUTE - ATTRIBUTE_BASE);
		register(REV, REV_ATTRIBUTE - ATTRIBUTE_BASE);
		register(ID, ID_ATTRIBUTE - ATTRIBUTE_BASE);
		register(FROM, FROM_ATTRIBUTE - ATTRIBUTE_BASE);
		register(TO, TO_ATTRIBUTE - ATTRIBUTE_BASE);
	}

	@Override
	public void register(final String attribute, final int key) {
		if (keyToAttribute.containsKey(key)) {
			throw new IllegalArgumentException(String.format("attribute adaption with key %s already registered", key));
		}
		attributeToKey.put(attribute, key);
		keyToAttribute.put(key, attribute);
	}

	@Override
	public Integer toKey(final String attribute) {
		return attributeToKey.get(attribute);
	}

	@Override
	public String fromKey(final int key) {
		return keyToAttribute.get(key);
	}

}
