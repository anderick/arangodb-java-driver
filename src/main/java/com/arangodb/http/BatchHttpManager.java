package com.arangodb.http;

import java.util.ArrayList;
import java.util.List;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoException;

/**
 * Created by fbartels on 10/22/14.
 */
public class BatchHttpManager extends HttpManager {

	private List<BatchPart> callStack = new ArrayList<BatchPart>();

	private InvocationObject currentObject;

	private boolean batchModeActive = false;

	public BatchHttpManager(final ArangoConfigure configure) {
		super(configure);
	}

	@Override
	public HttpResponseEntity execute(final HttpRequestEntity requestEntity) throws ArangoException {
		if (!this.isBatchModeActive()) {
			return super.execute(requestEntity);
		}

		final int id = callStack.size() + 1;
		callStack.add(new BatchPart(requestEntity.getType().toString(), buildUrl("", requestEntity),
				requestEntity.getBody(), requestEntity.getHeaders(), this.getCurrentObject(), id));
		this.setCurrentObject(null);
		final HttpResponseEntity responseEntity = new HttpResponseEntity();

		// http status
		responseEntity.setStatusCode(206);
		responseEntity.setStatusPhrase("Batch mode active, request has been stacked");
		responseEntity.setRequestId("request" + id);
		return responseEntity;
	}

	public List<BatchPart> getCallStack() {
		return callStack;
	}

	@Override
	public InvocationObject getCurrentObject() {
		return currentObject;
	}

	@Override
	public void setCurrentObject(final InvocationObject currentObject) {
		this.currentObject = currentObject;
	}

	public boolean isBatchModeActive() {
		return batchModeActive;
	}

	public void setBatchModeActive(final boolean batchModeActive) {
		this.batchModeActive = batchModeActive;
	}

	public void emptyCallStack() {
		this.callStack = new ArrayList<BatchPart>();
	}
}