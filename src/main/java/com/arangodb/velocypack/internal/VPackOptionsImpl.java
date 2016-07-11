package com.arangodb.velocypack.internal;

import com.arangodb.velocypack.VPack.VPackOptions;
import com.arangodb.velocypack.VPackBuilder.BuilderOptions;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackOptionsImpl implements VPackOptions, BuilderOptions {

	protected boolean buildUnindexedArrays;
	protected boolean buildUnindexedObjects;

	public VPackOptionsImpl() {
		super();
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
