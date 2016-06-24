package com.arangodb.velocypack.defaults;

import com.arangodb.velocypack.VPackKeyTranslator;
import com.arangodb.velocypack.VPackBuilder.BuilderOptions;
import com.arangodb.velocypack.VPack.VPackOptions;
import com.arangodb.velocypack.VPackSlice.SliceOptions;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackDefaultOptions implements VPackOptions, SliceOptions, BuilderOptions {

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
	public VPackKeyTranslator getKeyTranslator() {
		return translator;
	}

	@Override
	public void setKeyTranslator(final VPackKeyTranslator translator) {
		this.translator = translator;
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
