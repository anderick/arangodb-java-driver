package com.arangodb.velocypack.defaults;

import com.arangodb.velocypack.VPack.VPackOptions;
import com.arangodb.velocypack.VPackBuilder.BuilderOptions;
import com.arangodb.velocypack.VPackKeyTranslator;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackDefaultOptions implements VPackOptions, BuilderOptions {

	protected VPackKeyTranslator translator;
	protected boolean buildUnindexedArrays;
	protected boolean buildUnindexedObjects;

	public VPackDefaultOptions() {
		super();
		translator = new VPackDefaultKeyTranslator();
		buildUnindexedArrays = false;
		buildUnindexedObjects = false;
	}

	@Override
	public boolean isBuildUnindexedArrays() {
		return buildUnindexedArrays;
	}

	@Override
	public void setBuildUnindexedArrays(final boolean buildUnindexedArrays) {
		this.buildUnindexedArrays = buildUnindexedArrays;
	}

	@Override
	public boolean isBuildUnindexedObjects() {
		return buildUnindexedObjects;
	}

	@Override
	public void setBuildUnindexedObjects(final boolean buildUnindexedObjects) {
		this.buildUnindexedObjects = buildUnindexedObjects;
	}

}
