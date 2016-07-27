package com.arangodb.http;

import java.util.Map;

import com.arangodb.entity.EntityFactory;
import com.arangodb.velocypack.VPackSlice;

/**
 * Created by fbartels on 10/22/14.
 */
public class BatchPart {

	private String method;
	private String url;
	private VPackSlice body;
	private Map<String, Object> headers;
	private InvocationObject invocationObject;
	private String id;

	public BatchPart(final String method, final String url, final VPackSlice body, final Map<String, Object> headers,
		final InvocationObject invocationObject, final int id) {
		this.method = method;
		this.url = url;
		this.body = body;
		this.headers = headers;
		this.invocationObject = invocationObject;
		this.id = "request" + id;
	}

	public VPackSlice getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "BatchPart{" + "method='" + method + '\'' + ", url='" + url + '\'' + ", body='"
				+ EntityFactory.toJson(body) + '\'' + '}';
	}

	public InvocationObject getInvocationObject() {
		return this.invocationObject;
	}

	public void setInvocationObject(final InvocationObject invocationObject) {
		this.invocationObject = invocationObject;
	}

	public void setBody(final VPackSlice body) {
		this.body = body;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(final String method) {
		this.method = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(final Map<String, Object> headers) {
		this.headers = headers;
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}
}
