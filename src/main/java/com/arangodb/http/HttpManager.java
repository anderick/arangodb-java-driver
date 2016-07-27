/*
 * Copyright (C) 2012 tamtam180
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.arangodb.http;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.ArangoConfigure;
import com.arangodb.ArangoException;
import com.arangodb.entity.EntityFactory;
import com.arangodb.http.HttpRequestEntity.RequestType;
import com.arangodb.util.IOUtils;
import com.arangodb.velocypack.VPackSlice;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @author a-brandt
 * 
 */
public class HttpManager {

	private static final String USERAGENT = "Mozilla/5.0 (compatible; ArangoDB-JavaDriver/1.1; +http://mt.orz.at/)";
	private static final ContentType CONTENT_TYPE = ContentType.create("application/x-velocypack");

	private static Logger logger = LoggerFactory.getLogger(HttpManager.class);

	private PoolingHttpClientConnectionManager cm;
	private CloseableHttpClient client;

	private ArangoConfigure configure;

	private HttpResponseEntity preDefinedResponse;

	private HttpMode httpMode = HttpMode.SYNC;

	private List<String> jobIds = new ArrayList<String>();

	private final Map<String, InvocationObject> jobs = new HashMap<String, InvocationObject>();

	public enum HttpMode {
		SYNC, ASYNC, FIREANDFORGET
	}

	public HttpManager(final ArangoConfigure configure) {
		this.configure = configure;
	}

	public ArangoConfigure getConfiguration() {
		return this.configure;
	}

	public void init() {
		// socket factory for HTTP
		final ConnectionSocketFactory plainsf = new PlainConnectionSocketFactory();

		// socket factory for HTTPS
		final SSLConnectionSocketFactory sslsf = initSSLConnectionSocketFactory();

		// register socket factories
		final Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", plainsf).register("https", sslsf).build();

		// ConnectionManager
		cm = new PoolingHttpClientConnectionManager(r);
		cm.setDefaultMaxPerRoute(configure.getMaxPerConnection());
		cm.setMaxTotal(configure.getMaxTotalConnection());

		final Builder custom = RequestConfig.custom();

		// RequestConfig
		if (configure.getConnectionTimeout() >= 0) {
			custom.setConnectTimeout(configure.getConnectionTimeout());
		}
		if (configure.getTimeout() >= 0) {
			custom.setConnectionRequestTimeout(configure.getTimeout());
			custom.setSocketTimeout(configure.getTimeout());
		}

		cm.setValidateAfterInactivity(configure.getValidateAfterInactivity());

		final RequestConfig requestConfig = custom.build();

		final HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
		builder.setConnectionManager(cm);

		// KeepAlive Strategy
		final ConnectionKeepAliveStrategy keepAliveStrategy = new ConnectionKeepAliveStrategy() {

			@Override
			public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
				return HttpManager.this.getKeepAliveDuration(response);
			}

		};
		builder.setKeepAliveStrategy(keepAliveStrategy);

		// Retry Handler
		builder.setRetryHandler(new DefaultHttpRequestRetryHandler(configure.getRetryCount(), false));

		// Proxy
		addProxyToBuilder(builder);

		// Client
		client = builder.build();
	}

	private long getKeepAliveDuration(final HttpResponse response) {
		// Honor 'keep-alive' header
		final HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
		while (it.hasNext()) {
			final HeaderElement he = it.nextElement();
			final String param = he.getName();
			final String value = he.getValue();
			if (value != null && "timeout".equalsIgnoreCase(param)) {
				try {
					return Long.parseLong(value) * 1000L;
				} catch (final NumberFormatException ignore) {
					// ignore this exception
				}
			}
		}
		// otherwise keep alive for 30 seconds
		return 30L * 1000L;
	}

	private void addProxyToBuilder(final HttpClientBuilder builder) {
		if (configure.getProxyHost() != null && configure.getProxyPort() != 0) {
			final HttpHost proxy = new HttpHost(configure.getProxyHost(), configure.getProxyPort(), "http");

			final DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
			builder.setRoutePlanner(routePlanner);
		}
	}

	public void destroy() {
		if (cm != null) {
			cm.shutdown();
		}
		configure = null;
		try {
			if (client != null) {
				client.close();
			}
		} catch (final IOException e) {
		}
	}

	public HttpMode getHttpMode() {
		return httpMode;
	}

	public void setHttpMode(final HttpMode httpMode) {
		this.httpMode = httpMode;
	}

	public HttpResponseEntity doGet(final String url) throws ArangoException {
		return doGet(url, null);
	}

	public HttpResponseEntity doGet(final String url, final Map<String, Object> params) throws ArangoException {
		return doHeadGetDelete(RequestType.GET, url, null, params);
	}

	public HttpResponseEntity doGet(
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params) throws ArangoException {
		return doHeadGetDelete(RequestType.GET, url, headers, params);
	}

	public HttpResponseEntity doGet(
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params,
		final String username,
		final String password) throws ArangoException {
		return doHeadGetDelete(RequestType.GET, url, headers, params, username, password);
	}

	public HttpResponseEntity doHead(final String url, final Map<String, Object> params) throws ArangoException {
		return doHeadGetDelete(RequestType.HEAD, url, null, params);
	}

	public HttpResponseEntity doDelete(final String url, final Map<String, Object> params) throws ArangoException {
		return doHeadGetDelete(RequestType.DELETE, url, null, params);
	}

	public HttpResponseEntity doDelete(
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params) throws ArangoException {
		return doHeadGetDelete(RequestType.DELETE, url, headers, params);
	}

	public HttpResponseEntity doHeadGetDelete(
		final RequestType type,
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params) throws ArangoException {
		return doHeadGetDelete(type, url, headers, params, null, null);
	}

	public HttpResponseEntity doHeadGetDelete(
		final RequestType type,
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params,
		final String username,
		final String password) throws ArangoException {
		final HttpRequestEntity requestEntity = new HttpRequestEntity();
		requestEntity.setType(type);
		requestEntity.setUrl(url);
		requestEntity.setHeaders(headers);
		requestEntity.setParameters(params);
		requestEntity.setUsername(username);
		requestEntity.setPassword(password);
		return execute(requestEntity);
	}

	public HttpResponseEntity doPost(
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params,
		final VPackSlice body) throws ArangoException {
		return doPostPutPatch(RequestType.POST, url, headers, params, body, null);
	}

	public HttpResponseEntity doPost(final String url, final Map<String, Object> params, final VPackSlice body)
			throws ArangoException {
		return doPostPutPatch(RequestType.POST, url, null, params, body, null);
	}

	public HttpResponseEntity doPost(final String url, final Map<String, Object> params, final HttpEntity entity)
			throws ArangoException {
		return doPostPutPatch(RequestType.POST, url, null, params, null, entity);
	}

	public HttpResponseEntity doPostWithHeaders(
		final String url,
		final Map<String, Object> params,
		final HttpEntity entity,
		final Map<String, Object> headers,
		final VPackSlice body) throws ArangoException {
		return doPostPutPatch(RequestType.POST, url, headers, params, body, entity);
	}

	public HttpResponseEntity doPut(
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params,
		final VPackSlice body) throws ArangoException {
		return doPostPutPatch(RequestType.PUT, url, headers, params, body, null);
	}

	public HttpResponseEntity doPut(final String url, final Map<String, Object> params, final VPackSlice body)
			throws ArangoException {
		return doPostPutPatch(RequestType.PUT, url, null, params, body, null);
	}

	public HttpResponseEntity doPatch(
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params,
		final VPackSlice body) throws ArangoException {
		return doPostPutPatch(RequestType.PATCH, url, headers, params, body, null);
	}

	public HttpResponseEntity doPatch(final String url, final Map<String, Object> params, final VPackSlice body)
			throws ArangoException {
		return doPostPutPatch(RequestType.PATCH, url, null, params, body, null);
	}

	private HttpResponseEntity doPostPutPatch(
		final RequestType type,
		final String url,
		final Map<String, Object> headers,
		final Map<String, Object> params,
		final VPackSlice body,
		final HttpEntity entity) throws ArangoException {
		final HttpRequestEntity requestEntity = new HttpRequestEntity();
		requestEntity.setType(type);
		requestEntity.setUrl(url);
		requestEntity.setHeaders(headers);
		requestEntity.setParameters(params);
		requestEntity.setBody(body);
		requestEntity.setEntity(entity);
		return execute(requestEntity);
	}

	/**
	 * Executes the request and handles connect exceptions
	 * 
	 * @param requestEntity
	 *            the request
	 * @return the response of the request
	 * 
	 * @throws ArangoException
	 */
	public HttpResponseEntity execute(final HttpRequestEntity requestEntity) throws ArangoException {
		int retries = 0;
		final int connectRetryCount = configure.getConnectRetryCount();

		while (true) {
			try {
				return executeInternal(configure.getBaseUrl(), requestEntity);
			} catch (final SocketException ex) {
				retries++;
				if (connectRetryCount > 0 && retries > connectRetryCount) {
					logger.error(ex.getMessage(), ex);
					throw new ArangoException(ex);
				}

				if (configure.hasFallbackHost()) {
					configure.changeCurrentHost();
				}

				logger.warn(ex.getMessage(), ex);
				try {
					// 1000 milliseconds is one second.
					Thread.sleep(configure.getConnectRetryWait());
				} catch (final InterruptedException iex) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	/**
	 * Executes the request
	 * 
	 * @param requestEntity
	 *            the request
	 * @return the response of the request
	 * @throws ArangoException
	 */
	private HttpResponseEntity executeInternal(final String baseUrl, final HttpRequestEntity requestEntity)
			throws ArangoException, SocketException {

		final String url = buildUrl(baseUrl, requestEntity);

		logRequest(requestEntity, url);

		final HttpRequestBase request = buildHttpRequestBase(requestEntity, url);

		// common-header
		request.setHeader("User-Agent", USERAGENT);
		request.setHeader("Accept", CONTENT_TYPE.getMimeType());

		addOptionalHeaders(requestEntity, request);

		addHttpModeHeader(request);

		// Basic Auth
		final Credentials credentials = addCredentials(requestEntity, request);

		// CURL/HTTP Logger
		if (configure.isEnableCURLLogger()) {
			CURLLogger.log(url, requestEntity, credentials);
		}

		HttpResponseEntity responseEntity = null;
		if (preDefinedResponse != null) {
			responseEntity = preDefinedResponse;
		} else {
			final HttpResponse response = executeRequest(request);
			if (response != null) {
				try {
					responseEntity = buildHttpResponseEntity(requestEntity, response);
					consumeResponse(response);
				} catch (final IOException e) {
					throw new ArangoException(e);
				}

				if (this.getHttpMode().equals(HttpMode.ASYNC)) {
					final Map<String, String> map = responseEntity.getHeaders();
					this.addJob(map.get("X-Arango-Async-Id"), this.getCurrentObject());
				} else if (this.getHttpMode().equals(HttpMode.FIREANDFORGET)) {
					responseEntity = null;
				}
			}
		}

		return responseEntity;
	}

	private void consumeResponse(final HttpResponse response) throws IOException {
		final HttpEntity entity = response.getEntity();
		if (entity != null) {
			EntityUtils.consume(entity);
		}
	}

	private HttpResponse executeRequest(final HttpRequestBase request) throws SocketException, ArangoException {
		try {
			return client.execute(request);
		} catch (final SocketException ex) {
			// catch SocketException before IOException
			throw ex;
		} catch (final ClientProtocolException e) {
			throw new ArangoException(e);
		} catch (final IOException e) {
			throw new ArangoException(e);
		}
	}

	private HttpResponseEntity buildHttpResponseEntity(
		final HttpRequestEntity requestEntity,
		final HttpResponse response) throws IOException {
		final HttpResponseEntity responseEntity = new HttpResponseEntity();

		// http status
		final StatusLine status = response.getStatusLine();
		responseEntity.setStatusCode(status.getStatusCode());
		responseEntity.setStatusPhrase(status.getReasonPhrase());

		if (logger.isDebugEnabled()) {
			logger.debug("[RES]http-{}: statusCode={}", requestEntity.getType(), responseEntity.getStatusCode());
		}

		// ヘッダの処理
		// // TODO etag特殊処理は削除する。
		final Header etagHeader = response.getLastHeader("etag");
		if (etagHeader != null) {
			responseEntity.setEtag(etagHeader.getValue().replace("\"", ""));
		}
		// ヘッダをMapに変換する
		responseEntity.setHeaders(new TreeMap<String, String>());
		for (final Header header : response.getAllHeaders()) {
			responseEntity.getHeaders().put(header.getName(), header.getValue());
		}

		// レスポンスの取得
		final HttpEntity entity = response.getEntity();
		if (entity != null) {
			final Header contentType = entity.getContentType();
			if (contentType != null) {
				responseEntity.setContentType(contentType.getValue());
				if (responseEntity.isDumpResponse()) {
					responseEntity.setStream(entity.getContent());
					if (logger.isDebugEnabled()) {
						logger.debug("[RES]http-{}: stream, {}", requestEntity.getType(), contentType.getValue());
					}
				}
			}
			// Close stream in this method.
			if (responseEntity.getStream() == null) {
				final VPackSlice content = new VPackSlice(IOUtils.toByteArray(entity.getContent()));
				responseEntity.setContent(content);
				// responseEntity.setText(IOUtils.toString(entity.getContent()));
				if (logger.isDebugEnabled()) {
					logger.debug("[RES]http-{}: text={}", requestEntity.getType(), EntityFactory.toJson(content));
				}
			}
		}
		return responseEntity;
	}

	private void addHttpModeHeader(final HttpRequestBase request) {
		if (this.getHttpMode().equals(HttpMode.ASYNC)) {
			request.addHeader("x-arango-async", "store");
		} else if (this.getHttpMode().equals(HttpMode.FIREANDFORGET)) {
			request.addHeader("x-arango-async", "true");
		}
	}

	private Credentials addCredentials(final HttpRequestEntity requestEntity, final HttpRequestBase request)
			throws ArangoException {
		Credentials credentials = null;
		if (requestEntity.getUsername() != null && requestEntity.getPassword() != null) {
			credentials = new UsernamePasswordCredentials(requestEntity.getUsername(), requestEntity.getPassword());
		} else if (configure.getUser() != null && configure.getPassword() != null) {
			credentials = new UsernamePasswordCredentials(configure.getUser(), configure.getPassword());
		}
		if (credentials != null) {
			final BasicScheme basicScheme = new BasicScheme();
			try {
				request.addHeader(basicScheme.authenticate(credentials, request, null));
			} catch (final AuthenticationException e) {
				throw new ArangoException(e);
			}
		}
		return credentials;
	}

	private void addOptionalHeaders(final HttpRequestEntity requestEntity, final HttpRequestBase request) {
		if (requestEntity.getHeaders() != null) {
			for (final Entry<String, Object> keyValue : requestEntity.getHeaders().entrySet()) {
				request.setHeader(keyValue.getKey(), keyValue.getValue().toString());
			}
		}
	}

	private HttpRequestBase buildHttpRequestBase(final HttpRequestEntity requestEntity, final String url) {
		HttpRequestBase request;
		switch (requestEntity.getType()) {
		case POST:
			final HttpPost post = new HttpPost(url);
			configureBodyParams(requestEntity, post);
			request = post;
			break;
		case PUT:
			final HttpPut put = new HttpPut(url);
			configureBodyParams(requestEntity, put);
			request = put;
			break;
		case PATCH:
			final HttpPatch patch = new HttpPatch(url);
			configureBodyParams(requestEntity, patch);
			request = patch;
			break;
		case HEAD:
			request = new HttpHead(url);
			break;
		case DELETE:
			request = new HttpDelete(url);
			break;
		case GET:
		default:
			request = new HttpGet(url);
			break;
		}
		return request;
	}

	private void logRequest(final HttpRequestEntity requestEntity, final String url) {
		if (logger.isDebugEnabled()) {
			if (requestEntity.getType() == RequestType.POST || requestEntity.getType() == RequestType.PUT
					|| requestEntity.getType() == RequestType.PATCH) {
				logger.debug("[REQ]http-{}: url={}, headers={}, body={}", new Object[] { requestEntity.getType(), url,
						requestEntity.getHeaders(), EntityFactory.toJson(requestEntity.getBody()) });
				// TODO toJson into EntityFactory
			} else {
				logger.debug("[REQ]http-{}: url={}, headers={}",
					new Object[] { requestEntity.getType(), url, requestEntity.getHeaders() });
			}
		}
	}

	public static String buildUrl(final String baseUrl, final HttpRequestEntity requestEntity) {
		if (requestEntity.getParameters() != null && !requestEntity.getParameters().isEmpty()) {
			final String paramString = URLEncodedUtils.format(toList(requestEntity.getParameters()), "utf-8");
			if (requestEntity.getUrl().contains("?")) {
				return baseUrl + requestEntity.getUrl() + "&" + paramString;
			} else {
				return baseUrl + requestEntity.getUrl() + "?" + paramString;
			}
		}
		return baseUrl + requestEntity.getUrl();
	}

	private static List<NameValuePair> toList(final Map<String, Object> parameters) {
		final ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>(parameters.size());
		for (final Entry<String, Object> param : parameters.entrySet()) {
			if (param.getValue() != null) {
				paramList.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
			}
		}
		return paramList;
	}

	public static void configureBodyParams(
		final HttpRequestEntity requestEntity,
		final HttpEntityEnclosingRequestBase request) {

		if (requestEntity.getEntity() != null) {
			request.setEntity(requestEntity.getEntity());
		} else if (requestEntity.getBody() != null) {
			final VPackSlice body = requestEntity.getBody();
			request.setEntity(new ByteArrayEntity(body.getVpack(), body.getStart(), body.getByteSize(), CONTENT_TYPE));
			// request.setEntity(new StringEntity(requestEntity.getBodyText(),
			// APPLICATION_JSON_UTF8));
		}

	}

	public static boolean is400Error(final ArangoException e) {
		return e.getCode() == HttpStatus.SC_BAD_REQUEST;
	}

	public static boolean is404Error(final ArangoException e) {
		return e.getCode() == HttpStatus.SC_NOT_FOUND;
	}

	public static boolean is412Error(final ArangoException e) {
		return e.getCode() == HttpStatus.SC_PRECONDITION_FAILED;
	}

	public static boolean is200(final HttpResponseEntity res) {
		return res.getStatusCode() == HttpStatus.SC_OK;
	}

	public static boolean is400Error(final HttpResponseEntity res) {
		return res.getStatusCode() == HttpStatus.SC_BAD_REQUEST;
	}

	public static boolean is404Error(final HttpResponseEntity res) {
		return res.getStatusCode() == HttpStatus.SC_NOT_FOUND;
	}

	public static boolean is412Error(final HttpResponseEntity res) {
		return res.getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED;
	}

	public CloseableHttpClient getClient() {
		return client;
	}

	public InvocationObject getCurrentObject() {
		// do nothing here (used in BatchHttpManager)
		return null;
	}

	public void setCurrentObject(final InvocationObject currentObject) {
		// do nothing here (used in BatchHttpManager)
	}

	public void setPreDefinedResponse(final HttpResponseEntity preDefinedResponse) {
		this.preDefinedResponse = preDefinedResponse;
	}

	public List<String> getJobIds() {
		return jobIds;
	}

	public Map<String, InvocationObject> getJobs() {
		return jobs;
	}

	public void addJob(final String jobId, final InvocationObject invocationObject) {
		jobIds.add(jobId);
		jobs.put(jobId, invocationObject);
	}

	public String getLastJobId() {
		return jobIds.isEmpty() ? null : jobIds.get(jobIds.size() - 1);
	}

	public void resetJobs() {
		this.jobIds = new ArrayList<String>();
		this.jobs.clear();

	}

	private SSLConnectionSocketFactory initSSLConnectionSocketFactory() {
		SSLConnectionSocketFactory sslsf;
		if (configure.getSslContext() != null) {
			sslsf = new SSLConnectionSocketFactory(configure.getSslContext());
		} else {
			sslsf = new SSLConnectionSocketFactory(SSLContexts.createSystemDefault());
		}
		return sslsf;
	}

}
