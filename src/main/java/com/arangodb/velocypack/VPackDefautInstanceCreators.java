package com.arangodb.velocypack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Mark - mark@arangodb.com
 *
 */
@SuppressWarnings("rawtypes")
public class VPackDefautInstanceCreators {

	public static void registerInstanceCreators(final VPackParser parser) {
		parser.regitserInstanceCreator(Collection.class, new CollectionInstanceCreator());
		parser.regitserInstanceCreator(List.class, new ListInstanceCreator());
		parser.regitserInstanceCreator(Set.class, new SetInstanceCreator());
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

}
