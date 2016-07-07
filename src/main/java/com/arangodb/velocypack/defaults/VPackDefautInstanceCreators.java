package com.arangodb.velocypack.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPackInstanceCreator;

/**
 * @author Mark - mark@arangodb.com
 *
 */
@SuppressWarnings("rawtypes")
public class VPackDefautInstanceCreators {

	private VPackDefautInstanceCreators() {
		super();
	}

	public static void registerInstanceCreators(final VPack parser) {
		parser.regitserInstanceCreator(Collection.class, new CollectionInstanceCreator());
		parser.regitserInstanceCreator(List.class, new ListInstanceCreator());
		parser.regitserInstanceCreator(Set.class, new SetInstanceCreator());
		parser.regitserInstanceCreator(Map.class, new MapInstanceCreator());
	}

	protected static class CollectionInstanceCreator implements VPackInstanceCreator<Collection> {
		@Override
		public Collection createInstance() {
			return new ArrayList();
		}
	}

	protected static class ListInstanceCreator implements VPackInstanceCreator<List> {
		@Override
		public List createInstance() {
			return new ArrayList();
		}
	}

	protected static class SetInstanceCreator implements VPackInstanceCreator<Set> {
		@Override
		public Set createInstance() {
			return new HashSet();
		}
	}

	protected static class MapInstanceCreator implements VPackInstanceCreator<Map> {
		@Override
		public Map createInstance() {
			return new HashMap();
		}

	}

}
