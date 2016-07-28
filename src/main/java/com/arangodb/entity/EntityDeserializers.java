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

package com.arangodb.entity;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arangodb.entity.CollectionEntity.Figures;
import com.arangodb.entity.QueryCachePropertiesEntity.CacheMode;
import com.arangodb.entity.ReplicationApplierState.LastError;
import com.arangodb.entity.ReplicationApplierState.Progress;
import com.arangodb.entity.ReplicationInventoryEntity.Collection;
import com.arangodb.entity.ReplicationInventoryEntity.CollectionParameter;
import com.arangodb.entity.ReplicationLoggerStateEntity.Client;
import com.arangodb.entity.StatisticsDescriptionEntity.Figure;
import com.arangodb.entity.StatisticsDescriptionEntity.Group;
import com.arangodb.entity.StatisticsEntity.FigureValue;
import com.arangodb.entity.marker.VertexEntity;
import com.arangodb.util.DateUtils;
import com.arangodb.velocypack.VPackDeserializationContext;
import com.arangodb.velocypack.VPackDeserializer;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackException;
import com.arangodb.velocypack.exception.VPackParserException;

/**
 * Entity deserializer , internally used.
 *
 * @author tamtam180 - kirscheless at gmail.com
 * @author mark - mark at arangodb.com
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class EntityDeserializers {

	private static final String ADAPTIVE_POLLING = "adaptivePolling";

	private static final String AUTO_START = "autoStart";

	private static final String CHUNK_SIZE = "chunkSize";

	private static final String REQUEST_TIMEOUT = "requestTimeout";

	private static final String CONNECT_TIMEOUT = "connectTimeout";

	private static final String MAX_CONNECT_RETRIES = "maxConnectRetries";

	private static final String PASSWORD = "password";

	private static final String USERNAME = "username";

	private static final String DATABASE = "database";

	private static final String ENDPOINT = "endpoint";

	private static final String SERVER = "server";

	private static final String ETAG = "etag";

	private static final String ERROR_MESSAGE = "errorMessage";

	private static final String ERROR_NUM = "errorNum";

	private static final String CODE = "code";

	private static final String ERROR = "error";

	private static final String RESULT = "result";

	private static final String VERSION = "version";

	private static final String PATHS = "paths";

	private static final String VERTICES = "vertices";

	private static final String SIZE = "size";

	private static final String COUNT = "count";

	private static final String ALIVE = "alive";

	private static final String WARNINGS = "warnings";

	private static final String ALT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private static final String DO_COMPACT = "doCompact";

	private static final String CHECKSUM = "checksum";

	private static final String KEY_OPTIONS = "keyOptions";

	private static final String TYPE = "type";

	private static final String FIGURES = "figures";

	private static final String REVISION = "revision";

	private static final String JOURNAL_SIZE = "journalSize";

	private static final String IS_VOLATILE = "isVolatile";

	private static final String IS_SYSTEM = "isSystem";

	private static final String WAIT_FOR_SYNC = "waitForSync";

	private static final String STATUS = "status";

	private static final String ID = "id";

	private static final String NAME = "name";

	private static final String EXTRA = "extra";

	private static final String ACTIVE = "active";

	private static final String COLLECTIONS = "collections";

	private static final String LAST_ERROR = "lastError";

	private static final String LAST_AVAILABLE_CONTINUOUS_TICK = "lastAvailableContinuousTick";

	private static final String LAST_PROCESSED_CONTINUOUS_TICK = "lastProcessedContinuousTick";

	private static final String LAST_APPLIED_CONTINUOUS_TICK = "lastAppliedContinuousTick";

	private static final String INDEXES = "indexes";

	private static final String EDGES = "edges";

	private static final String STATE = "state";

	private static final String FILE_SIZE = "fileSize";

	private static final String UPDATED = "updated";

	private static final String REPLACED = "replaced";

	private static final String DELETED = "deleted";

	private static final String MESSAGE = "message";

	private static final String CREATED = "created";
	private static final String ERRORS = "errors";
	private static final String EMPTY = "empty";
	private static final String IGNORED = "ignored";
	private static final String DETAILS = "details";

	private static final String TIME = "time";

	private static Logger logger = LoggerFactory.getLogger(EntityDeserializers.class);

	private static class ClassHolder {
		private final Class<?>[] clazz;
		private int idx;

		ClassHolder(final Class<?>... clazz) {
			this.clazz = clazz;
			this.idx = 0;
		}

		public boolean isEmpty() {
			return clazz == null || clazz.length == 0;
		}

		public Class<?> get() {
			if (isEmpty()) {
				return null;
			}
			return clazz[idx];
		}

		public Class<?> next() {
			if (isEmpty()) {
				return null;
			}
			if (idx + 1 >= clazz.length) {
				throw new IllegalStateException("idx max-over!! idx=" + (idx + 1));
			}
			return clazz[++idx];
		}

		public boolean hasNext() {
			if (isEmpty()) {
				return false;
			}
			if (idx + 1 >= clazz.length) {
				return false;
			}
			return true;
		}

		public Class<?> back() {
			if (isEmpty()) {
				return null;
			}
			if (idx - 1 < 0) {
				throw new IllegalStateException("idx min-over!! idx=" + (idx - 1));
			}
			return clazz[--idx];
		}
	}

	private static ThreadLocal<ClassHolder> parameterizedBridger = new ThreadLocal<ClassHolder>();

	private EntityDeserializers() {
		// this is a helper class
	}

	public static void setParameterized(final Class<?>... clazz) {
		parameterizedBridger.set(new ClassHolder(clazz));
	}

	public static void removeParameterized() {
		parameterizedBridger.remove();
	}

	private static Class<?> getParameterized() {
		final ClassHolder holder = parameterizedBridger.get();
		if (holder == null) {
			return null;
		}
		return holder.get();
	}

	private static boolean hasNextParameterized() {
		final ClassHolder holder = parameterizedBridger.get();
		if (holder == null) {
			return false;
		}
		return holder.hasNext();
	}

	private static Class<?> nextParameterized() {
		final ClassHolder holder = parameterizedBridger.get();
		if (holder == null) {
			return null;
		}
		return holder.next();
	}

	private static <T extends BaseEntity> T deserializeBaseParameter(final VPackSlice obj, final T entity)
			throws VPackException {
		final VPackSlice error = obj.get(ERROR);
		if (error.isBoolean()) {
			entity.setError(error.getAsBoolean());
		}
		final VPackSlice code = obj.get(CODE);
		if (code.isNumber()) {
			entity.setCode(code.getAsInt());
		}
		final VPackSlice errorNum = obj.get(ERROR_NUM);
		if (errorNum.isNumber()) {
			entity.setErrorNumber(errorNum.getAsInt());
		}
		final VPackSlice errorMessage = obj.get(ERROR_MESSAGE);
		if (errorMessage.isString()) {
			entity.setErrorMessage(errorMessage.getAsString());
		}
		final VPackSlice etag = obj.get(ETAG);
		if (etag.isString()) {
			entity.setEtag(etag.getAsString());
		}
		return entity;
	}

	private static <T extends DocumentHolder> T deserializeDocumentParameter(final VPackSlice obj, final T entity)
			throws VPackException {
		final VPackSlice rev = obj.get(BaseDocument.REV);
		if (rev.isString()) {
			entity.setDocumentRevision(rev.getAsString());
		}
		final VPackSlice id = obj.get(BaseDocument.ID);
		if (id.isString()) {
			entity.setDocumentHandle(id.getAsString());
		}
		final VPackSlice key = obj.get(BaseDocument.KEY);
		if (key.isString()) {
			entity.setDocumentKey(key.getAsString());
		}
		return entity;
	}

	public static class DefaultEntityDeserializer implements VPackDeserializer<DefaultEntity> {

		@Override
		public DefaultEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			return deserializeBaseParameter(vpack, new DefaultEntity());
		}
	}

	public static class VersionDeserializer implements VPackDeserializer<ArangoVersion> {

		@Override
		public ArangoVersion deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ArangoVersion entity = deserializeBaseParameter(vpack, new ArangoVersion());
			final VPackSlice server = vpack.get(SERVER);
			if (server.isString()) {
				entity.setServer(server.getAsString());
			}
			final VPackSlice version = vpack.get(VERSION);
			if (version.isString()) {
				entity.setVersion(version.getAsString());
			}
			return entity;
		}
	}

	public static class ArangoUnixTimeDeserializer implements VPackDeserializer<ArangoUnixTime> {

		@Override
		public ArangoUnixTime deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ArangoUnixTime entity = deserializeBaseParameter(vpack, new ArangoUnixTime());
			final VPackSlice vtime = vpack.get(TIME);
			if (vtime.isNumber()) {
				entity.setTime(vtime.getAsDouble());
				final String time = String.valueOf(entity.getTime());
				entity.setSecond((int) entity.getTime());

				final int pos = time.indexOf('.');
				entity.setMicrosecond(
					(pos >= 0 && pos + 1 != time.length()) ? Integer.parseInt(time.substring(pos + 1)) : 0);
			}
			return entity;
		}
	}

	public static class FiguresDeserializer implements VPackDeserializer<Figures> {

		@Override
		public Figures deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final Figures entity = new Figures();
			final VPackSlice alive = vpack.get(ALIVE);
			if (alive.isObject()) {
				entity.setAliveCount(alive.get(COUNT).getAsLong());
				entity.setAliveSize(alive.get(SIZE).getAsLong());
			}
			final VPackSlice dead = vpack.get("dead");
			if (dead.isObject()) {
				entity.setDeadCount(dead.get(COUNT).getAsLong());
				entity.setDeadSize(dead.get(SIZE).getAsLong());
				entity.setDeadDeletion(dead.get("deletion").getAsLong());
			}
			final VPackSlice datafiles = vpack.get("datafiles");
			if (datafiles.isObject()) {
				entity.setDatafileCount(datafiles.get(COUNT).getAsLong());
				entity.setDatafileFileSize(datafiles.get(FILE_SIZE).getAsLong());
			}
			final VPackSlice journals = vpack.get("journals");
			if (journals.isObject()) {
				entity.setJournalsCount(journals.get(COUNT).getAsLong());
				entity.setJournalsFileSize(journals.get(FILE_SIZE).getAsLong());
			}
			final VPackSlice compactors = vpack.get("compactors");
			if (compactors.isObject()) {
				entity.setCompactorsCount(compactors.get(COUNT).getAsLong());
				entity.setCompactorsFileSize(compactors.get(FILE_SIZE).getAsLong());
			}
			final VPackSlice indexes = vpack.get(INDEXES);
			if (indexes.isObject()) {
				entity.setIndexesCount(indexes.get(COUNT).getAsLong());
				entity.setIndexesSize(indexes.get(SIZE).getAsLong());
			}
			final VPackSlice lastTick = vpack.get("lastTick");
			if (lastTick.isNumber()) {
				entity.setLastTick(lastTick.getAsLong());
			}
			final VPackSlice uncollectedLogfileEntries = vpack.get("uncollectedLogfileEntries");
			if (uncollectedLogfileEntries.isNumber()) {
				entity.setUncollectedLogfileEntries(uncollectedLogfileEntries.getAsLong());
			}
			return entity;
		}
	}

	public static class CollectionKeyOptionDeserializer implements VPackDeserializer<CollectionKeyOption> {

		@Override
		public CollectionKeyOption deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final CollectionKeyOption entity = new CollectionKeyOption();
			final VPackSlice type = vpack.get("type");
			if (type.isString()) {
				entity.setType(type.getAsString());
			}
			final VPackSlice allowUserKeys = vpack.get("allowUserKeys");
			if (allowUserKeys.isBoolean()) {
				entity.setAllowUserKeys(allowUserKeys.getAsBoolean());
			}
			final VPackSlice increment = vpack.get("increment");
			if (increment.isNumber()) {
				entity.setIncrement(increment.getAsLong());
			}
			final VPackSlice offset = vpack.get("offset");
			if (offset.isNumber()) {
				entity.setOffset(offset.getAsLong());
			}
			return entity;
		}
	}

	public static class CollectionEntityDeserializer implements VPackDeserializer<CollectionEntity> {

		@Override
		public CollectionEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final CollectionEntity entity = deserializeBaseParameter(vpack, new CollectionEntity());
			final VPackSlice name = vpack.get(NAME);
			if (name.isString()) {
				entity.setName(name.getAsString());
			}
			final VPackSlice id = vpack.get(ID);
			if (id.isNumber()) {
				entity.setId(id.getAsLong());
			}
			final VPackSlice status = vpack.get(STATUS);
			if (!status.isNone() && !status.isNull()) {
				entity.setStatus(context.deserialize(status, CollectionStatus.class));
			}
			final VPackSlice waitForSync = vpack.get(WAIT_FOR_SYNC);
			if (waitForSync.isBoolean()) {
				entity.setWaitForSync(waitForSync.getAsBoolean());
			}
			final VPackSlice isSystem = vpack.get(IS_SYSTEM);
			if (isSystem.isBoolean()) {
				entity.setIsSystem(isSystem.getAsBoolean());
			}
			final VPackSlice isVolatile = vpack.get(IS_VOLATILE);
			if (isVolatile.isBoolean()) {
				entity.setIsVolatile(isVolatile.getAsBoolean());
			}
			final VPackSlice journalSize = vpack.get(JOURNAL_SIZE);
			if (journalSize.isNumber()) {
				entity.setJournalSize(journalSize.getAsLong());
			}
			final VPackSlice count = vpack.get(COUNT);
			if (count.isNumber()) {
				entity.setCount(count.getAsLong());
			}
			final VPackSlice revision = vpack.get(REVISION);
			if (revision.isNumber()) {
				entity.setRevision(revision.getAsLong());
			}
			final VPackSlice figures = vpack.get(FIGURES);
			if (figures.isObject()) {
				entity.setFigures(context.deserialize(figures, Figures.class));
			}
			final VPackSlice type = vpack.get(TYPE);
			if (type.isNumber()) {
				entity.setType(CollectionType.valueOf(type.getAsInt()));
			}
			final VPackSlice keyOptions = vpack.get(KEY_OPTIONS);
			if (keyOptions.isObject()) {
				entity.setKeyOptions(context.deserialize(keyOptions, CollectionKeyOption.class));
			}
			final VPackSlice checksum = vpack.get(CHECKSUM);
			if (checksum.isNumber()) {
				entity.setChecksum(checksum.getAsLong());
			}
			final VPackSlice doCompact = vpack.get(DO_COMPACT);
			if (doCompact.isBoolean()) {
				entity.setDoCompact(doCompact.getAsBoolean());
			}
			return entity;
		}

	}

	public static class CollectionsEntityDeserializer implements VPackDeserializer<CollectionsEntity> {

		@Override
		public CollectionsEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final CollectionsEntity entity = deserializeBaseParameter(vpack, new CollectionsEntity());
			final VPackSlice result = vpack.get(RESULT);
			if (result.isArray()) {
				entity.setCollections(context.deserialize(result, List.class, CollectionEntity.class));
			} else {
				entity.setCollections(new ArrayList<CollectionEntity>());
			}
			return entity;
		}
	}

	public static class AqlfunctionsEntityDeserializer implements VPackDeserializer<AqlFunctionsEntity> {

		@Override
		public AqlFunctionsEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}

			final Iterator<VPackSlice> iterator = vpack.iterator();
			final Map<String, String> functions = new HashMap<String, String>();
			while (iterator.hasNext()) {
				final VPackSlice e = iterator.next();
				functions.put(e.get("name").getAsString(), e.get(CODE).getAsString());
			}
			return new AqlFunctionsEntity(functions);
		}

	}

	public static class JobsEntityDeserializer implements VPackDeserializer<JobsEntity> {

		@Override
		public JobsEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}

			final Iterator<VPackSlice> iterator = vpack.iterator();
			final List<String> jobs = new ArrayList<String>();
			while (iterator.hasNext()) {
				final VPackSlice e = iterator.next();
				jobs.add(e.getAsString());
			}
			return new JobsEntity(jobs);
		}

	}

	public static class CursorEntityDeserializer implements VPackDeserializer<CursorEntity> {

		@Override
		public CursorEntity<?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final CursorEntity<Object> entity = deserializeBaseParameter(vpack, new CursorEntity<Object>());
			final VPackSlice result = vpack.get(RESULT);
			if (result.isArray() && result.getLength() > 0) {
				getResultObjects(context, entity, result);
			} else {
				entity.setResults(Collections.emptyList());
			}
			final VPackSlice hasMore = vpack.get("hasMore");
			if (hasMore.isBoolean()) {
				entity.setHasMore(hasMore.getAsBoolean());
			}
			final VPackSlice count = vpack.get(COUNT);
			if (count.isNumber()) {
				entity.setCount(count.getAsInt());
			}
			final VPackSlice id = vpack.get(ID);
			if (id.isNumber()) {
				entity.setCursorId(id.getAsLong());
			}
			final VPackSlice cached = vpack.get("cached");
			if (cached.isBoolean()) {
				entity.setCached(cached.getAsBoolean());
			}
			final VPackSlice bindVars = vpack.get("bindVars");
			if (bindVars.isArray()) {
				entity.setBindVars(context.deserialize(bindVars, List.class, String.class));
			}
			entity.setWarnings(new ArrayList<WarningEntity>());
			final VPackSlice extra = vpack.get(EXTRA);
			if (extra.isArray()) {
				entity.setExtra(context.deserialize(extra, Map.class, String.class, Object.class));
				getFullCount(entity);
				getWarnings(entity);
			}
			return entity;
		}

		private void getResultObjects(
			final VPackDeserializationContext context,
			final CursorEntity<Object> entity,
			final VPackSlice array) throws VPackParserException {
			final Class<?> clazz = getParameterized();
			final boolean withDocument = DocumentEntity.class.isAssignableFrom(clazz);
			if (withDocument) {
				nextParameterized();
			}
			try {
				final List<Object> list = new ArrayList<Object>(array.size());
				for (int i = 0, imax = array.size(); i < imax; i++) {
					list.add(context.deserialize(array.get(i), clazz));
				}
				entity.setResults(list);
			} finally {
				if (withDocument) {
					backParameterized();
				}
			}
		}

		private void getWarnings(final CursorEntity<Object> entity) {
			if (entity.getExtra().containsKey(WARNINGS)) {
				final Object object = entity.getExtra().get(WARNINGS);
				if (object instanceof List<?>) {
					final List<?> l = (List<?>) entity.getExtra().get(WARNINGS);
					getWarningsFromList(entity, l);
				}
			}
		}

		private void getWarningsFromList(final CursorEntity<Object> entity, final List<?> l) {
			for (final Object o : l) {
				if (o instanceof Map<?, ?>) {
					final Map<?, ?> m = (Map<?, ?>) o;
					if (m.containsKey(CODE) && m.get(CODE) instanceof Double && m.containsKey(MESSAGE)
							&& m.get(MESSAGE) instanceof String) {
						final Long code = ((Double) m.get(CODE)).longValue();
						final String message = (String) m.get(MESSAGE);
						entity.getWarnings().add(new WarningEntity(code, message));
					}
				}
			}
		}

		private void getFullCount(final CursorEntity<Object> entity) {
			final Map<String, Object> extra = entity.getExtra();
			if (extra.containsKey("stats") && extra.get("stats") instanceof Map<?, ?>) {
				final Map<?, ?> m = (Map<?, ?>) extra.get("stats");
				if (m.containsKey("fullCount") && m.get("fullCount") instanceof Double) {
					final Double v = (Double) m.get("fullCount");
					entity.setFullCount(v.intValue());
				}
			}
		}

		private Class<?> backParameterized() {
			final ClassHolder holder = parameterizedBridger.get();
			if (holder == null) {
				return null;
			}
			return holder.back();
		}

	}

	public static class DocumentEntityDeserializer implements VPackDeserializer<DocumentEntity> {

		@Override
		public DocumentEntity<?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (!vpack.isObject()) {
				return new DocumentEntity<Object>();
			}
			final DocumentEntity<Object> entity = new DocumentEntity<Object>();
			deserializeDocumentParameter(vpack, entity);

			// 他のフィールドはリフレクションで。 (TODO: Annotationのサポートと上記パラメータを弾く)
			final Class<?> clazz = getParameterized();
			if (clazz != null) {
				entity.setEntity(context.deserialize(vpack, clazz));

				if (clazz.getName().equalsIgnoreCase(BaseDocument.class.getName())) {
					// iterate all key/value pairs of the jsonObject and
					// determine its class(String, Number, Boolean, HashMap,
					// List)
					((BaseDocument) entity.getEntity())
							.setProperties(DeserializeSingleEntry.deserializeJsonObject(vpack));
				}
			}

			return entity;
		}
	}

	public static class DeserializeSingleEntry {

		private static final List<String> nonProperties = Arrays.asList("_id", "_rev", "_key");

		private DeserializeSingleEntry() {
			// this is a helper class
		}

		/**
		 * deserialize any jsonElement
		 *
		 * @param vpack
		 * @return a object
		 */
		public static Object deserializeJsonElement(final VPackSlice vpack) {
			if (vpack.isObject()) {
				return deserializeJsonObject(vpack);
			} else if (vpack.isArray()) {
				return deserializeJsonArray(vpack);
			} else {
				return deserializeJsonPrimitive(vpack);
			}
		}

		/**
		 * deserializes a JsonObject into a Map<String, Object>
		 *
		 * @param vpack
		 *            a jsonObject
		 * @return the deserialized jsonObject
		 */
		private static Map<String, Object> deserializeJsonObject(final VPackSlice vpack) {
			final Map<String, Object> result = new HashMap<String, Object>();
			for (int i = 0; i < vpack.getLength(); i++) {
				final String key = vpack.keyAt(i).getAsString();
				if (!nonProperties.contains(key)) {
					result.put(key, deserializeJsonElement(vpack.valueAt(i)));
				}
			}
			return result;
		}

		private static List<Object> deserializeJsonArray(final VPackSlice vpack) {
			final List<Object> tmpObjectList = new ArrayList<Object>();
			final Iterator<VPackSlice> iterator = vpack.iterator();
			while (iterator.hasNext()) {
				tmpObjectList.add(deserializeJsonElement(iterator.next()));
			}
			return tmpObjectList;
		}

		/**
		 * deserializes a jsonPrimitiv into the equivalent java primitive
		 *
		 * @param vpack
		 * @return null|String|Double|Boolean
		 */
		private static Object deserializeJsonPrimitive(final VPackSlice vpack) {
			if (vpack.isBoolean()) {
				return vpack.getAsBoolean();
			} else if (vpack.isNumber()) {
				return vpack.getAsDouble();
			} else if (vpack.isString()) {
				return vpack.getAsString();
			}
			return null;
		}

	}

	public static class DocumentsEntityDeserializer implements VPackDeserializer<DocumentsEntity> {

		@Override
		public DocumentsEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final DocumentsEntity entity = deserializeBaseParameter(vpack, new DocumentsEntity());
			final VPackSlice documents = vpack.get("documents");
			if (documents.isArray()) {
				entity.setDocuments(context.deserialize(documents, List.class, String.class));
			}
			return entity;
		}

	}

	public static class IndexEntityDeserializer implements VPackDeserializer<IndexEntity> {

		@Override
		public IndexEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final IndexEntity entity = deserializeBaseParameter(vpack, new IndexEntity());
			final VPackSlice id = vpack.get(ID);
			if (id.isString()) {
				entity.setId(id.getAsString());
			}
			final VPackSlice vtype = vpack.get(TYPE);
			if (vtype.isString()) {
				final String type = vtype.getAsString().toUpperCase(Locale.US);
				if (type.startsWith(IndexType.GEO.name())) {
					entity.setType(IndexType.GEO);
				} else {
					entity.setType(IndexType.valueOf(type));
				}
			}
			final VPackSlice fields = vpack.get("fields");
			if (fields.isArray()) {
				entity.setFields(context.deserialize(fields, List.class, String.class));
			}
			final VPackSlice geoJson = vpack.get("geoJson");
			if (geoJson.isBoolean()) {
				entity.setGeoJson(geoJson.getAsBoolean());
			}
			final VPackSlice isNewlyCreated = vpack.get("isNewlyCreated");
			if (isNewlyCreated.isBoolean()) {
				entity.setNewlyCreated(isNewlyCreated.getAsBoolean());
			}
			final VPackSlice unique = vpack.get("unique");
			if (unique.isBoolean()) {
				entity.setUnique(unique.getAsBoolean());
			}
			final VPackSlice sparse = vpack.get("sparse");
			if (sparse.isBoolean()) {
				entity.setSparse(sparse.getAsBoolean());
			}
			final VPackSlice size = vpack.get("size");
			if (size.isNumber()) {
				entity.setSize(size.getAsInt());
			}
			final VPackSlice minLength = vpack.get("minLength");
			if (minLength.isNumber()) {
				entity.setMinLength(minLength.getAsInt());
			}
			final VPackSlice selectivityEstimate = vpack.get("selectivityEstimate");
			if (selectivityEstimate.isNumber()) {
				entity.setSelectivityEstimate(selectivityEstimate.getAsDouble());
			}
			return entity;
		}

	}

	public static class IndexesEntityDeserializer implements VPackDeserializer<IndexesEntity> {

		@Override
		public IndexesEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {

			if (vpack.isNull()) {
				return null;
			}
			final IndexesEntity entity = deserializeBaseParameter(vpack, new IndexesEntity());
			final VPackSlice indexes = vpack.get(INDEXES);
			if (indexes.isArray()) {
				entity.setIndexes(context.deserialize(indexes, List.class, IndexEntity.class));
			}
			final VPackSlice identifiers = vpack.get("identifiers");
			if (identifiers.isObject()) {
				entity.setIdentifiers(context.deserialize(identifiers, Map.class, String.class, IndexEntity.class));
			}
			return entity;
		}

	}

	public static class AdminLogEntryEntityDeserializer implements VPackDeserializer<AdminLogEntity> {

		@Override
		public AdminLogEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}

			final AdminLogEntity entity = deserializeBaseParameter(vpack, new AdminLogEntity());
			// 全ての要素は必ずあることが前提なのでhasチェックはしない
			final int[] lids = context.deserialize(vpack.get("lid"), int[].class);
			final int[] levels = context.deserialize(vpack.get("level"), int[].class);
			final long[] timestamps = context.deserialize(vpack.get("timestamp"), long[].class);
			final String[] texts = context.deserialize(vpack.get("text"), String[].class);

			// 配列のサイズが全て同じであること
			if (lids.length != levels.length || lids.length != timestamps.length || lids.length != texts.length) {
				throw new IllegalStateException("each parameters returns wrong length.");
			}

			entity.setLogs(new ArrayList<AdminLogEntity.LogEntry>(lids.length));
			for (int i = 0; i < lids.length; i++) {
				final AdminLogEntity.LogEntry entry = new AdminLogEntity.LogEntry();
				entry.setLid(lids[i]);
				entry.setLevel(levels[i]);
				entry.setTimestamp(new Date(timestamps[i] * 1000L));
				entry.setText(texts[i]);
				entity.getLogs().add(entry);
			}
			final VPackSlice totalAmount = vpack.get("totalAmount");
			if (totalAmount.isNumber()) {
				entity.setTotalAmount(totalAmount.getAsInt());
			}
			return entity;
		}

	}

	public static class StatisticsEntityDeserializer implements VPackDeserializer<StatisticsEntity> {

		@Override
		public StatisticsEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}

			final StatisticsEntity entity = deserializeBaseParameter(vpack, new StatisticsEntity());

			deserializeSystem(vpack, entity);

			deserializeClient(context, vpack, entity);

			deserializeServer(vpack, entity);

			return entity;
		}

		private void deserializeServer(final VPackSlice obj, final StatisticsEntity entity) throws VPackException {
			final VPackSlice server = obj.get(SERVER);
			if (server.isObject()) {
				entity.setServer(new StatisticsEntity.Server());
				final VPackSlice uptime = server.get("uptime");
				if (uptime.isNumber()) {
					entity.getServer().setUptime(uptime.getAsDouble());
				}
			}
		}

		private void deserializeClient(
			final VPackDeserializationContext context,
			final VPackSlice obj,
			final StatisticsEntity entity) throws VPackException {
			final VPackSlice client = obj.get("client");
			if (client.isObject()) {
				final StatisticsEntity.Client cli = new StatisticsEntity.Client();
				cli.setFigures(new TreeMap<String, StatisticsEntity.FigureValue>());
				entity.setClient(cli);
				final VPackSlice httpConnections = client.get("httpConnections");
				if (httpConnections.isNumber()) {
					cli.setHttpConnections(httpConnections.getAsInt());
				}
				for (int i = 0; i < client.getLength(); i++) {
					final String key = client.keyAt(i).getAsString();
					if (!"httpConnections".equals(key)) {
						final VPackSlice f = client.valueAt(i);
						final FigureValue fv = new FigureValue();
						fv.setSum(f.get("sum").getAsDouble());
						fv.setCount(f.get(COUNT).getAsLong());
						fv.setCounts(context.deserialize(f.get("counts"), long[].class));
						cli.getFigures().put(key, fv);
					}
				}
			}
		}

		private void deserializeSystem(final VPackSlice obj, final StatisticsEntity entity) throws VPackException {
			final VPackSlice system = obj.get("system");
			if (system.isObject()) {
				final StatisticsEntity.System sys = new StatisticsEntity.System();
				entity.setSystem(sys);

				final VPackSlice minorPageFaults = system.get("minorPageFaults");
				if (minorPageFaults.isNumber()) {
					sys.setMinorPageFaults(minorPageFaults.getAsLong());
				}
				final VPackSlice majorPageFaults = system.get("majorPageFaults");
				if (majorPageFaults.isNumber()) {
					sys.setMajorPageFaults(majorPageFaults.getAsLong());
				}
				final VPackSlice userTime = system.get("userTime");
				if (userTime.isNumber()) {
					sys.setUserTime(userTime.getAsDouble());
				}
				final VPackSlice systemTime = system.get("systemTime");
				if (systemTime.isNumber()) {
					sys.setSystemTime(systemTime.getAsDouble());
				}
				final VPackSlice numberOfThreads = system.get("numberOfThreads");
				if (numberOfThreads.isNumber()) {
					sys.setNumberOfThreads(numberOfThreads.getAsInt());
				}
				final VPackSlice residentSize = system.get("residentSize");
				if (residentSize.isNumber()) {
					sys.setResidentSize(residentSize.getAsLong());
				}
				final VPackSlice virtualSize = system.get("virtualSize");
				if (virtualSize.isNumber()) {
					sys.setVirtualSize(virtualSize.getAsLong());
				}
			}
		}
	}

	public static class StatisticsDescriptionEntityDeserializer
			implements VPackDeserializer<StatisticsDescriptionEntity> {

		@Override
		public StatisticsDescriptionEntity deserialize(
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final StatisticsDescriptionEntity entity = deserializeBaseParameter(vpack,
				new StatisticsDescriptionEntity());
			final VPackSlice groups = vpack.get("groups");
			if (groups.isArray()) {
				entity.setGroups(new ArrayList<StatisticsDescriptionEntity.Group>(groups.size()));
				for (int i = 0, imax = groups.size(); i < imax; i++) {
					final VPackSlice g = groups.get(i);
					final Group group = new Group();
					group.setGroup(g.get("group").getAsString());
					group.setName(g.get("name").getAsString());
					group.setDescription(g.get("description").getAsString());
					entity.getGroups().add(group);
				}
			}
			final VPackSlice figures = vpack.get(FIGURES);
			if (figures.isArray()) {
				entity.setFigures(new ArrayList<StatisticsDescriptionEntity.Figure>(figures.size()));
				for (int i = 0, imax = figures.size(); i < imax; i++) {
					final VPackSlice f = figures.get(i);
					final Figure figure = new Figure();
					figure.setGroup(f.get("group").getAsString());
					figure.setIdentifier(f.get("identifier").getAsString());
					figure.setName(f.get("name").getAsString());
					figure.setDescription(f.get("description").getAsString());
					figure.setType(f.get("type").getAsString());
					figure.setUnits(f.get("units").getAsString());
					final VPackSlice cuts = f.get("cuts");
					if (cuts.isArray()) {
						figure.setCuts(context.deserialize(cuts, BigDecimal[].class));
					}
					entity.getFigures().add(figure);
				}
			}
			return entity;
		}
	}

	public static class ScalarExampleEntityDeserializer implements VPackDeserializer<ScalarExampleEntity> {

		@Override
		public ScalarExampleEntity<?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ScalarExampleEntity<?> entity = deserializeBaseParameter(vpack, new ScalarExampleEntity<Object>());
			final VPackSlice document = vpack.get("document");
			if (document.isObject()) {
				entity.setDocument(context.deserialize(document, DocumentEntity.class));
			}
			return entity;
		}

	}

	public static class SimpleByResultEntityDeserializer implements VPackDeserializer<SimpleByResultEntity> {

		@Override
		public SimpleByResultEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final SimpleByResultEntity entity = deserializeBaseParameter(vpack, new SimpleByResultEntity());
			final VPackSlice deleted = vpack.get(DELETED);
			if (deleted.isNumber()) {
				entity.setCount(deleted.getAsInt());
				entity.setDeleted(deleted.getAsInt());
			}
			final VPackSlice replaced = vpack.get(REPLACED);
			if (replaced.isNumber()) {
				entity.setCount(replaced.getAsInt());
				entity.setDeleted(replaced.getAsInt());
			}
			final VPackSlice updated = vpack.get(UPDATED);
			if (updated.isNumber()) {
				entity.setCount(updated.getAsInt());
				entity.setUpdated(updated.getAsInt());
			}
			return entity;
		}

	}

	public static class TransactionResultEntityDeserializer implements VPackDeserializer<TransactionResultEntity> {

		@Override
		public TransactionResultEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final TransactionResultEntity entity = deserializeBaseParameter(vpack, new TransactionResultEntity());
			final VPackSlice result = vpack.get(RESULT);
			if (!result.isNull()) {
				if (result.isObject()) {
					entity.setResult(result);
				} else if (result.isBoolean()) {
					entity.setResult(result.getAsBoolean());
				} else if (result.isNumber()) {
					entity.setResult(result.getAsNumber());
				} else if (result.isString()) {
					entity.setResult(result.getAsString());
				}
			}
			return entity;
		}

	}

	public static class UserEntityDeserializer implements VPackDeserializer<UserEntity> {

		@Override
		public UserEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final UserEntity entity = deserializeBaseParameter(vpack, new UserEntity());
			final VPackSlice user = vpack.get("user");
			if (user.isString()) { // MEMO:
				// RequestはusernameなのにResponseは何故userなのか。。
				entity.setUsername(user.getAsString());
			}
			final VPackSlice password = vpack.get(PASSWORD);
			if (password.isString()) {
				entity.setPassword(password.getAsString());
			}
			final VPackSlice active = vpack.get(ACTIVE);
			if (active.isBoolean()) {
				entity.setActive(active.getAsBoolean());
			} else {
				final VPackSlice authData = vpack.get("authData");
				if (authData.isObject()) {
					// for simple/all requsts
					final VPackSlice authDataActive = authData.get(ACTIVE);
					if (authDataActive.isBoolean()) {
						entity.setActive(authDataActive.getAsBoolean());
					}
				}
			}
			final VPackSlice extra = vpack.get(EXTRA);
			if (extra.isObject()) {
				entity.setExtra(context.deserialize(extra, Map.class, String.class, Object.class));
			} else {
				final VPackSlice userData = vpack.get("userData");
				// for simple/all requsts
				entity.setExtra(
					userData.isObject() ? context.deserialize(userData, Map.class, String.class, Object.class)
							: new HashMap<String, Object>());
			}
			return entity;
		}

	}

	public static class ImportResultEntityDeserializer implements VPackDeserializer<ImportResultEntity> {

		@Override
		public ImportResultEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ImportResultEntity entity = deserializeBaseParameter(vpack, new ImportResultEntity());
			final VPackSlice created = vpack.get(CREATED);
			if (created.isNumber()) {
				entity.setCreated(created.getAsInt());
			}
			final VPackSlice errors = vpack.get(ERRORS);
			if (errors.isNumber()) {
				entity.setErrors(errors.getAsInt());
			}
			final VPackSlice empty = vpack.get(EMPTY);
			if (empty.isNumber()) {
				entity.setEmpty(empty.getAsInt());
			}
			final VPackSlice updated = vpack.get(UPDATED);
			if (updated.isNumber()) {
				entity.setUpdated(updated.getAsInt());
			}
			final VPackSlice ignored = vpack.get(IGNORED);
			if (ignored.isNumber()) {
				entity.setIgnored(ignored.getAsInt());
			}
			final VPackSlice details = vpack.get(DETAILS);
			if (details.isArray()) {
				for (final Iterator<VPackSlice> iterator = details.iterator(); iterator.hasNext();) {
					entity.getDetails().add(iterator.next().getAsString());
				}
			}
			return entity;
		}

	}

	public static class DatabaseEntityDeserializer implements VPackDeserializer<DatabaseEntity> {

		@Override
		public DatabaseEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final DatabaseEntity entity = deserializeBaseParameter(vpack, new DatabaseEntity());
			final VPackSlice result = vpack.get(RESULT);
			if (result.isObject()) {
				final VPackSlice name = result.get("name");
				if (name.isString()) {
					entity.setName(name.getAsString());
				}
				final VPackSlice id = result.get(ID);
				if (id.isString()) {
					entity.setId(id.getAsString());
				}
				final VPackSlice path = result.get("path");
				if (path.isString()) {
					entity.setPath(path.getAsString());
				}
				final VPackSlice isSystem = result.get(IS_SYSTEM);
				if (isSystem.isBoolean()) {
					entity.setSystem(isSystem.getAsBoolean());
				}
			}
			return entity;
		}
	}

	public static class StringsResultEntityDeserializer implements VPackDeserializer<StringsResultEntity> {

		@Override
		public StringsResultEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final StringsResultEntity entity = deserializeBaseParameter(vpack, new StringsResultEntity());
			final VPackSlice result = vpack.get(RESULT);
			if (result.isArray()) {
				entity.setResult(context.deserialize(result, List.class, String.class));
			}
			return entity;
		}

	}

	public static class BooleanResultEntityDeserializer implements VPackDeserializer<BooleanResultEntity> {

		@Override
		public BooleanResultEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final BooleanResultEntity entity = deserializeBaseParameter(vpack, new BooleanResultEntity());
			final VPackSlice result = vpack.get(RESULT);
			if (result.isBoolean()) {
				entity.setResult(result.getAsBoolean());
			}
			return entity;
		}
	}

	public static class EndpointDeserializer implements VPackDeserializer<Endpoint> {

		@Override
		public Endpoint deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final Endpoint entity = new Endpoint();
			entity.setDatabases(context.deserialize(vpack.get("databases"), List.class, String.class));
			entity.setEndpoint(vpack.get("endpoint").getAsString());
			return entity;
		}
	}

	public static class DocumentResultEntityDeserializer implements VPackDeserializer<DocumentResultEntity> {

		@Override
		public DocumentResultEntity<?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final DocumentResultEntity<Object> entity = deserializeBaseParameter(vpack,
				new DocumentResultEntity<Object>());
			final VPackSlice result = vpack.get(RESULT);
			if (result.isArray()) {
				entity.setResult(context.deserialize(result, List.class, DocumentEntity.class));
			} else if (result.isObject()) {
				final DocumentEntity<Object> doc = context.deserialize(result, DocumentEntity.class);
				final List<DocumentEntity<Object>> list = new ArrayList<DocumentEntity<Object>>(1);
				list.add(doc);
				entity.setResult(list);
			} else {
				throw new IllegalStateException("result type is not array or object:" + result.head());
			}
			return entity;
		}
	}

	public static class ReplicationStateDeserializer implements VPackDeserializer<ReplicationState> {

		@Override
		public ReplicationState deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationState entity = new ReplicationState();
			entity.setRunning(vpack.get("running").getAsBoolean());
			entity.setLastLogTick(vpack.get("lastLogTick").getAsLong());
			entity.setTotalEvents(vpack.get("totalEvents").getAsLong());
			entity.setTime(DateUtils.parse(vpack.get(TIME)));
			return entity;
		}
	}

	public static class ReplicationInventoryEntityDeserializer
			implements VPackDeserializer<ReplicationInventoryEntity> {

		@Override
		public ReplicationInventoryEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationInventoryEntity entity = deserializeBaseParameter(vpack, new ReplicationInventoryEntity());
			final VPackSlice collections = vpack.get(COLLECTIONS);
			if (collections.isArray()) {
				entity.setCollections(new ArrayList<ReplicationInventoryEntity.Collection>(collections.size()));
				for (int i = 0, imax = collections.size(); i < imax; i++) {
					final VPackSlice elem = collections.get(i);
					final Collection col = new Collection();
					final VPackSlice parameters = elem.get("parameters");
					if (parameters.isObject()) {
						addCollectionParameters(col, parameters);
					}
					final VPackSlice indexes = elem.get(INDEXES);
					if (indexes.isArray()) {
						col.setIndexes(context.deserialize(indexes, List.class, IndexEntity.class));
					}
					entity.getCollections().add(col);
				}
			}
			final VPackSlice state = vpack.get(STATE);
			if (state.isObject()) {
				entity.setState(context.deserialize(state, ReplicationState.class));
			}
			final VPackSlice tick = vpack.get("tick");
			if (tick.isNumber()) {
				entity.setTick(tick.getAsLong());
			}
			return entity;
		}

		private void addCollectionParameters(final Collection col, final VPackSlice parameters) throws VPackException {
			col.setParameter(new CollectionParameter());
			final VPackSlice version = parameters.get(VERSION);
			if (version.isNumber()) {
				col.getParameter().setVersion(version.getAsInt());
			}
			final VPackSlice type = parameters.get(TYPE);
			if (type.isNumber()) {
				col.getParameter().setType(CollectionType.valueOf(type.getAsInt()));
			}
			final VPackSlice cid = parameters.get("cid");
			if (cid.isNumber()) {
				col.getParameter().setCid(cid.getAsLong());
			}
			final VPackSlice deleted = parameters.get(DELETED);
			if (deleted.isBoolean()) {
				col.getParameter().setDeleted(deleted.getAsBoolean());
			}
			final VPackSlice doCompact = parameters.get(DO_COMPACT);
			if (doCompact.isBoolean()) {
				col.getParameter().setDoCompact(doCompact.getAsBoolean());
			}
			final VPackSlice maximalSize = parameters.get("maximalSize");
			if (maximalSize.isNumber()) {
				col.getParameter().setMaximalSize(maximalSize.getAsLong());
			}
			final VPackSlice name = parameters.get("name");
			if (name.isString()) {
				col.getParameter().setName(name.getAsString());
			}
			final VPackSlice isVolatile = parameters.get(IS_VOLATILE);
			if (isVolatile.isBoolean()) {
				col.getParameter().setVolatile(isVolatile.getAsBoolean());
			}
			final VPackSlice waitForSync = parameters.get(WAIT_FOR_SYNC);
			if (waitForSync.isBoolean()) {
				col.getParameter().setWaitForSync(waitForSync.getAsBoolean());
			}
		}
	}

	public static class ReplicationDumpRecordDeserializer implements VPackDeserializer<ReplicationDumpRecord> {

		@Override
		public ReplicationDumpRecord<?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationDumpRecord<DocumentEntity<Object>> entity = new ReplicationDumpRecord<DocumentEntity<Object>>();
			final VPackSlice tick = vpack.get("tick");
			if (tick.isNumber()) {
				entity.setTick(tick.getAsLong());
			}
			final VPackSlice type = vpack.get(TYPE);
			if (type.isNumber()) {
				entity.setType(ReplicationEventType.valueOf(type.getAsInt()));
			}
			final VPackSlice key = vpack.get("key");
			if (key.isString()) {
				entity.setKey(key.getAsString());
			}
			final VPackSlice rev = vpack.get("rev");
			if (rev.isNumber()) {
				entity.setRev(rev.getAsLong());
			}
			final VPackSlice data = vpack.get("data");
			if (data.isObject()) {
				entity.setData(context.deserialize(data, DocumentEntity.class));
			}
			return entity;
		}

	}

	public static class ReplicationSyncEntityDeserializer implements VPackDeserializer<ReplicationSyncEntity> {

		@Override
		public ReplicationSyncEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationSyncEntity entity = deserializeBaseParameter(vpack, new ReplicationSyncEntity());
			final VPackSlice collections = vpack.get(COLLECTIONS);
			if (collections.isArray()) {
				entity.setCollections(context.deserialize(collections, List.class, CollectionEntity.class));
			}
			final VPackSlice lastLogTick = vpack.get("lastLogTick");
			if (lastLogTick.isNumber()) {
				entity.setLastLogTick(lastLogTick.getAsLong());
			}
			return entity;
		}
	}

	public static class MapAsEntityDeserializer implements VPackDeserializer<MapAsEntity> {

		@Override
		public MapAsEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final MapAsEntity entity = deserializeBaseParameter(vpack, new MapAsEntity());
			entity.setMap(context.deserialize(vpack, Map.class, String.class, Object.class));
			return entity;
		}

	}

	public static class ReplicationLoggerConfigEntityDeserializer
			implements VPackDeserializer<ReplicationLoggerConfigEntity> {

		@Override
		public ReplicationLoggerConfigEntity deserialize(
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationLoggerConfigEntity entity = deserializeBaseParameter(vpack,
				new ReplicationLoggerConfigEntity());
			final VPackSlice autoStart = vpack.get(AUTO_START);
			if (autoStart.isBoolean()) {
				entity.setAutoStart(autoStart.getAsBoolean());
			}
			final VPackSlice logRemoteChanges = vpack.get("logRemoteChanges");
			if (logRemoteChanges.isBoolean()) {
				entity.setLogRemoteChanges(logRemoteChanges.getAsBoolean());
			}
			final VPackSlice maxEvents = vpack.get("maxEvents");
			if (maxEvents.isNumber()) {
				entity.setMaxEvents(maxEvents.getAsLong());
			}
			final VPackSlice maxEventsSize = vpack.get("maxEventsSize");
			if (maxEventsSize.isNumber()) {
				entity.setMaxEventsSize(maxEventsSize.getAsLong());
			}
			return entity;
		}
	}

	public static class ReplicationApplierConfigEntityDeserializer
			implements VPackDeserializer<ReplicationApplierConfigEntity> {

		@Override
		public ReplicationApplierConfigEntity deserialize(
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationApplierConfigEntity entity = deserializeBaseParameter(vpack,
				new ReplicationApplierConfigEntity());
			final VPackSlice endpoint = vpack.get(ENDPOINT);
			if (endpoint.isString()) {
				entity.setEndpoint(endpoint.getAsString());
			}
			final VPackSlice database = vpack.get(DATABASE);
			if (database.isString()) {
				entity.setDatabase(database.getAsString());
			}
			final VPackSlice username = vpack.get(USERNAME);
			if (username.isString()) {
				entity.setUsername(username.getAsString());
			}
			final VPackSlice password = vpack.get(PASSWORD);
			if (password.isString()) {
				entity.setPassword(password.getAsString());
			}
			final VPackSlice maxConnectRetries = vpack.get(MAX_CONNECT_RETRIES);
			if (maxConnectRetries.isNumber()) {
				entity.setMaxConnectRetries(maxConnectRetries.getAsInt());
			}
			final VPackSlice connectTimeout = vpack.get(CONNECT_TIMEOUT);
			if (connectTimeout.isNumber()) {
				entity.setConnectTimeout(connectTimeout.getAsInt());
			}
			final VPackSlice requestTimeout = vpack.get(REQUEST_TIMEOUT);
			if (requestTimeout.isNumber()) {
				entity.setRequestTimeout(requestTimeout.getAsInt());
			}
			final VPackSlice chunkSize = vpack.get(CHUNK_SIZE);
			if (chunkSize.isNumber()) {
				entity.setChunkSize(chunkSize.getAsInt());
			}
			final VPackSlice autoStart = vpack.get(AUTO_START);
			if (autoStart.getAsBoolean()) {
				entity.setAutoStart(autoStart.getAsBoolean());
			}
			final VPackSlice adaptivePolling = vpack.get(ADAPTIVE_POLLING);
			if (adaptivePolling.isBoolean()) {
				entity.setAdaptivePolling(adaptivePolling.getAsBoolean());
			}
			return entity;
		}

	}

	public static class ReplicationApplierStateDeserializer implements VPackDeserializer<ReplicationApplierState> {

		@Override
		public ReplicationApplierState deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationApplierState state = new ReplicationApplierState();
			final VPackSlice running = vpack.get("running");
			if (running.isBoolean()) {
				state.setRunning(running.getAsBoolean());
			}
			deserializeTicks(vpack, state);
			final VPackSlice time = vpack.get(TIME);
			state.setTime(DateUtils.parse(time));
			final VPackSlice totalRequests = vpack.get("totalRequests");
			if (totalRequests.isNumber()) {
				state.setTotalRequests(totalRequests.getAsLong());
			}
			final VPackSlice totalFailedConnects = vpack.get("totalFailedConnects");
			if (totalFailedConnects.isNumber()) {
				state.setTotalFailedConnects(totalFailedConnects.getAsLong());
			}
			final VPackSlice totalEvents = vpack.get("totalEvents");
			if (totalEvents.isNumber()) {
				state.setTotalEvents(totalEvents.getAsLong());
			}
			deserializeLastError(vpack, state);
			deserializeProgress(vpack, state);
			return state;
		}

		private void deserializeTicks(final VPackSlice obj, final ReplicationApplierState state) throws VPackException {
			final VPackSlice applied = obj.get(LAST_APPLIED_CONTINUOUS_TICK);
			if (applied.isNumber()) {
				state.setLastAppliedContinuousTick(applied.getAsLong());
			}
			final VPackSlice processed = obj.get(LAST_PROCESSED_CONTINUOUS_TICK);
			if (processed.isNumber()) {
				state.setLastProcessedContinuousTick(processed.getAsLong());
			}
			final VPackSlice available = obj.get(LAST_AVAILABLE_CONTINUOUS_TICK);
			if (available.isNumber()) {
				state.setLastAvailableContinuousTick(available.getAsLong());
			}
		}

		private void deserializeProgress(final VPackSlice obj, final ReplicationApplierState state)
				throws VPackException {
			final VPackSlice progress = obj.get("progress");
			if (progress.isObject()) {
				state.setProgress(new Progress());
				final VPackSlice failedConnects = progress.get("failedConnects");
				if (failedConnects.isNumber()) {
					state.getProgress().setFailedConnects(failedConnects.getAsLong());
				}
				final VPackSlice message = progress.get(MESSAGE);
				if (message.isString()) {
					state.getProgress().setMessage(message.getAsString());
				}
				final VPackSlice time = progress.get(TIME);
				state.getProgress().setTime(DateUtils.parse(time));
			}
		}

		private void deserializeLastError(final VPackSlice obj, final ReplicationApplierState state)
				throws VPackException {
			final VPackSlice lastError = obj.get(LAST_ERROR);
			if (lastError.isObject()) {
				state.setLastError(new LastError());
				final VPackSlice time = lastError.get(TIME);
				state.getLastError().setTime(DateUtils.parse(time));
				final VPackSlice errorNum = lastError.get(ERROR_NUM);
				if (errorNum.isNumber()) {
					state.getLastError().setErrorNum(errorNum.getAsInt());
				}
				final VPackSlice errorMessage = lastError.get(ERROR_MESSAGE);
				if (errorMessage.isString()) {
					state.getLastError().setErrorMessage(errorMessage.getAsString());
				}
			}
		}
	}

	public static class ReplicationApplierStateEntityDeserializer
			implements VPackDeserializer<ReplicationApplierStateEntity> {

		@Override
		public ReplicationApplierStateEntity deserialize(
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationApplierStateEntity entity = deserializeBaseParameter(vpack,
				new ReplicationApplierStateEntity());
			final VPackSlice endpoint = vpack.get(ENDPOINT);
			if (endpoint.isString()) {
				entity.setEndpoint(endpoint.getAsString());
			}
			final VPackSlice database = vpack.get(DATABASE);
			if (database.isString()) {
				entity.setDatabase(database.getAsString());
			}
			final VPackSlice server = vpack.get(SERVER);
			if (server.isObject()) {
				entity.setServerVersion(server.get(VERSION).getAsString());
				entity.setServerId(server.get("serverId").getAsString());
			}
			final VPackSlice state = vpack.get(STATE);
			if (state.isObject()) {
				entity.setState(context.deserialize(state, ReplicationApplierState.class));
			}
			return entity;
		}
	}

	public static class ReplicationLoggerStateEntityDeserializer
			implements VPackDeserializer<ReplicationLoggerStateEntity> {

		@Override
		public ReplicationLoggerStateEntity deserialize(
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ReplicationLoggerStateEntity entity = deserializeBaseParameter(vpack,
				new ReplicationLoggerStateEntity());
			final VPackSlice state = vpack.get(STATE);
			if (state.isObject()) {
				entity.setState(context.deserialize(state, ReplicationState.class));
			}
			final VPackSlice server = vpack.get(SERVER);
			if (server.isObject()) {
				entity.setServerVersion(server.get(VERSION).getAsString());
				entity.setServerId(server.get("serverId").getAsString());
			}
			final VPackSlice clients = vpack.get("clients");
			if (clients.isArray()) {
				entity.setClients(context.deserialize(clients, List.class, Client.class));
			}
			return entity;
		}

	}

	public static class ReplicationLoggerStateEntityClientDeserializer
			implements VPackDeserializer<ReplicationLoggerStateEntity.Client> {

		@Override
		public Client deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final Client client = new Client();
			final VPackSlice serverId = vpack.get("serverId");
			if (serverId.isString()) {
				client.setServerId(serverId.getAsString());
			}
			final VPackSlice lastServedTick = vpack.get("lastServedTick");
			if (lastServedTick.isNumber()) {
				client.setLastServedTick(lastServedTick.getAsLong());
			}
			final VPackSlice time = vpack.get(TIME);
			client.setTime(DateUtils.parse(time));
			return client;
		}
	}

	public static class GraphEntityDeserializer implements VPackDeserializer<GraphEntity> {

		private static final String COLLECTION = "collection";

		@Override
		public GraphEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}

			final GraphEntity entity = deserializeBaseParameter(vpack, new GraphEntity());

			final VPackSlice tmpGraph = vpack.get("graph");
			final VPackSlice graph = tmpGraph.isObject() ? tmpGraph : vpack;
			deserializeDocumentParameter(graph, entity);

			final VPackSlice name = graph.get("name");
			if (name.isString()) {
				entity.setName(name.getAsString());
			}
			final VPackSlice orphanCollections = graph.get("orphanCollections");
			if (orphanCollections.isArray()) {
				entity.setOrphanCollections(new ArrayList<String>());
				if (orphanCollections != null) {
					entity.setOrphanCollections(new ArrayList<String>(orphanCollections.size()));
					for (int i = 0, imax = orphanCollections.size(); i < imax; i++) {
						final String orphanCollection = orphanCollections.get(i).getAsString();
						entity.getOrphanCollections().add(orphanCollection);
					}
				}
			}
			final VPackSlice edgeDefinitions = graph.get("edgeDefinitions");
			if (edgeDefinitions.isArray()) {
				entity.setEdgeDefinitionsEntity(new EdgeDefinitionsEntity());
				if (edgeDefinitions != null) {
					addEdgeDefinitions(entity, edgeDefinitions);
				}
			}
			return entity;
		}

		private void addEdgeDefinitions(final GraphEntity entity, final VPackSlice edgeDefinitions)
				throws VPackException {
			for (int i = 0, imax = edgeDefinitions.size(); i < imax; i++) {
				final EdgeDefinitionEntity edgeDefinitionEntity = new EdgeDefinitionEntity();
				final VPackSlice edgeDefinition = edgeDefinitions.get(i);
				final VPackSlice collection = edgeDefinition.get(COLLECTION);
				if (collection.isString()) {
					edgeDefinitionEntity.setCollection(collection.getAsString());
				}
				final VPackSlice vfrom = edgeDefinition.get("from");
				if (vfrom.isArray()) {
					final List<String> from = new ArrayList<String>();
					final Iterator<VPackSlice> iterator = vfrom.iterator();
					while (iterator.hasNext()) {
						final VPackSlice e = iterator.next();
						from.add(e.getAsString());
					}

					edgeDefinitionEntity.setFrom(from);
				}
				final VPackSlice vto = edgeDefinition.get("to");
				if (vto.isArray()) {
					final List<String> to = new ArrayList<String>();
					final Iterator<VPackSlice> iterator = vto.iterator();
					while (iterator.hasNext()) {
						final VPackSlice e = iterator.next();
						to.add(e.getAsString());
					}
					edgeDefinitionEntity.setTo(to);
				}
				entity.getEdgeDefinitionsEntity().addEdgeDefinition(edgeDefinitionEntity);
			}
		}
	}

	public static class GraphsEntityDeserializer implements VPackDeserializer<GraphsEntity> {

		@Override
		public GraphsEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final GraphsEntity entity = deserializeBaseParameter(vpack, new GraphsEntity());
			final VPackSlice graphs = vpack.get("graphs");
			if (graphs.isArray()) {
				entity.setGraphs(context.deserialize(graphs, List.class, GraphEntity.class));
			}
			return entity;
		}

	}

	public static class DeleteEntityDeserializer implements VPackDeserializer<DeletedEntity> {

		@Override
		public DeletedEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final DeletedEntity entity = deserializeBaseParameter(vpack, new DeletedEntity());
			final VPackSlice deleted = vpack.get(DELETED);
			if (deleted.isBoolean()) {
				entity.setDeleted(deleted.getAsBoolean());
			}
			final VPackSlice removed = vpack.get("removed");
			if (removed.isBoolean()) {
				entity.setDeleted(removed.getAsBoolean());
			}
			return entity;
		}
	}

	public static class VertexEntityDeserializer implements VPackDeserializer<VertexEntity> {

		@Override
		public VertexEntity<?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final VertexEntity<Object> entity = deserializeBaseParameter(vpack, new VertexEntity<Object>());

			final VPackSlice tmpVertex = vpack.get("vertex");
			final VPackSlice vertex = tmpVertex.isObject() ? tmpVertex : vpack;
			deserializeDocumentParameter(vertex, entity);

			final Class<?> clazz = getParameterized();
			if (clazz != null) {
				entity.setEntity(context.deserialize(vertex, clazz));
			}
			return entity;
		}
	}

	public static class EdgeEntityDeserializer implements VPackDeserializer<EdgeEntity> {

		@Override
		public EdgeEntity<?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final EdgeEntity<Object> entity = deserializeBaseParameter(vpack, new EdgeEntity<Object>());

			final VPackSlice tmpEdge = vpack.get("edge");
			final VPackSlice edge = tmpEdge.isObject() ? tmpEdge : vpack;
			deserializeDocumentParameter(edge, entity);

			final VPackSlice from = edge.get("_from");
			if (from.isString()) {
				entity.setFromVertexHandle(from.getAsString());
			}
			final VPackSlice to = edge.get("_to");
			if (to.isString()) {
				entity.setToVertexHandle(to.getAsString());
			}
			// 他のフィールドはリフレクションで。 (TODO: Annotationのサポートと上記パラメータを弾く)
			final Class<?> clazz = getParameterized();
			if (clazz != null) {
				entity.setEntity(context.deserialize(edge, clazz));
			}
			return entity;
		}

	}

	public static class TraversalEntityDeserializer implements VPackDeserializer<TraversalEntity> {

		@Override
		public TraversalEntity<?, ?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final TraversalEntity<Object, Object> entity = deserializeBaseParameter(vpack,
				new TraversalEntity<Object, Object>());
			deserializeBaseParameter(vpack, entity);

			final VPackSlice result = getFirstResultAsJsonObject(vpack);
			if (result.isObject()) {
				final VPackSlice visited = result.get("visited");
				if (visited.isObject()) {
					final Class<?> vertexClazz = getParameterized();
					Class<?> edgeClazz = null;

					if (hasNextParameterized()) {
						edgeClazz = nextParameterized();
					}
					final VPackSlice vertices = visited.get(VERTICES);
					if (vertices.isArray()) {
						entity.setVertices(getVertices(vertexClazz, context, vertices));
					}
					final VPackSlice paths = visited.get(PATHS);
					if (!paths.isNull() && !paths.isNone()) {
						entity.setPaths(getPaths(context, visited, vertexClazz, edgeClazz));
					}
				}

			}
			return entity;
		}

		private List<PathEntity<Object, Object>> getPaths(
			final VPackDeserializationContext context,
			final VPackSlice visited,
			final Class<?> vertexClazz,
			final Class<?> edgeClazz) throws VPackException {
			final List<PathEntity<Object, Object>> pathEntities = new ArrayList<PathEntity<Object, Object>>();
			final VPackSlice paths = visited.get(PATHS);
			if (paths.isArray()) {
				for (int i = 0, imax = paths.size(); i < imax; i++) {
					final VPackSlice path = paths.get(i);
					final PathEntity<Object, Object> pathEntity = new PathEntity<Object, Object>();
					final VPackSlice edges = path.get(EDGES);
					if (edges.isArray()) {
						pathEntity.setEdges(getEdges(edgeClazz, context, edges));
					}
					final VPackSlice vertices = path.get(VERTICES);
					if (vertices.isArray()) {
						pathEntity.setVertices(getVertices(vertexClazz, context, vertices));
					}
					pathEntities.add(pathEntity);
				}
			}
			return pathEntities;
		}

	}

	public static class ShortestPathEntityDeserializer implements VPackDeserializer<ShortestPathEntity> {

		@Override
		public ShortestPathEntity<?, ?> deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final ShortestPathEntity<Object, Object> entity = deserializeBaseParameter(vpack,
				new ShortestPathEntity<Object, Object>());
			deserializeBaseParameter(vpack, entity);

			final VPackSlice result = getFirstResultAsJsonObject(vpack);
			if (result.isObject()) {
				final Class<?> vertexClazz = getParameterized();
				Class<?> edgeClazz = null;

				if (hasNextParameterized()) {
					edgeClazz = nextParameterized();
				}
				final VPackSlice distance = result.get("distance");
				if (distance.isNumber()) {
					entity.setDistance(distance.getAsLong());
				} else {
					entity.setDistance(-1L);
				}
				final VPackSlice edges = result.get(EDGES);
				if (edges.isArray()) {
					// new version >= 2.6
					entity.setEdges(getEdges(edgeClazz, context, edges));
				}
				final VPackSlice vertices = result.get(VERTICES);
				if (vertices.isArray()) {
					// new version >= 2.6
					entity.setVertices(getVertices(vertexClazz, context, vertices));
				}
				final VPackSlice paths = result.get(PATHS);
				if (!paths.isNull() && !paths.isNone()) {
					// old version < 2.6
					addOldPath(context, entity, result, vertexClazz, edgeClazz);
				}
			} else {
				entity.setDistance(-1L);
			}
			return entity;
		}

		private void addOldPath(
			final VPackDeserializationContext context,
			final ShortestPathEntity<Object, Object> entity,
			final VPackSlice result,
			final Class<?> vertexClazz,
			final Class<?> edgeClazz) throws VPackException {
			final VPackSlice paths = result.get(PATHS);
			if (paths != null && paths.size() > 0) {
				final VPackSlice path = paths.get(0);
				final VPackSlice edges = path.get(EDGES);
				if (edges.isArray()) {
					entity.setEdges(getEdges(edgeClazz, context, edges));
				}
				final VPackSlice vertices = path.get(VERTICES);
				if (vertices.isArray()) {
					entity.setVertices(getVertices(vertexClazz, context, vertices));
				}
			}
		}
	}

	public static class QueryCachePropertiesEntityDeserializer
			implements VPackDeserializer<QueryCachePropertiesEntity> {

		@Override
		public QueryCachePropertiesEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final QueryCachePropertiesEntity entity = deserializeBaseParameter(vpack, new QueryCachePropertiesEntity());
			final VPackSlice mode = vpack.get("mode");
			if (mode.isString()) {
				final String modeAsString = mode.getAsString();
				entity.setMode(CacheMode.valueOf(modeAsString));
			}
			final VPackSlice maxResults = vpack.get("maxResults");
			if (maxResults.isNumber()) {
				entity.setMaxResults(maxResults.getAsLong());
			}
			return entity;
		}

	}

	public static class QueriesResultEntityDeserializer implements VPackDeserializer<QueriesResultEntity> {

		@Override
		public QueriesResultEntity deserialize(final VPackSlice vpack, final VPackDeserializationContext context)
				throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final Iterator<VPackSlice> iterator = vpack.iterator();
			final List<QueryEntity> queries = new ArrayList<QueryEntity>();
			while (iterator.hasNext()) {
				final VPackSlice element = iterator.next();
				final QueryEntity entity = new QueryEntity();
				final VPackSlice id = element.get(ID);
				if (id.isString()) {
					entity.setId(id.getAsString());
					queries.add(entity);
				}
				final VPackSlice query = element.get("query");
				if (query.isString()) {
					entity.setQuery(query.getAsString());
				}
				final VPackSlice started = element.get("started");
				if (started.isString()) {
					final String str = started.getAsString();
					final SimpleDateFormat sdf = new SimpleDateFormat(ALT_DATE_TIME_FORMAT);
					try {
						entity.setStarted(sdf.parse(str));
					} catch (final ParseException e) {
						logger.debug("got ParseException for date string: " + str);
					}
				}
				final VPackSlice runTime = element.get("runTime");
				if (runTime.isNumber()) {
					entity.setRunTime(runTime.getAsDouble());
				}
			}
			return new QueriesResultEntity(queries);
		}

	}

	public static class QueryTrackingPropertiesEntityDeserializer
			implements VPackDeserializer<QueryTrackingPropertiesEntity> {

		@Override
		public QueryTrackingPropertiesEntity deserialize(
			final VPackSlice vpack,
			final VPackDeserializationContext context) throws VPackException {
			if (vpack.isNull()) {
				return null;
			}
			final QueryTrackingPropertiesEntity entity = deserializeBaseParameter(vpack,
				new QueryTrackingPropertiesEntity());
			final VPackSlice enabled = vpack.get("enabled");
			if (enabled.isBoolean()) {
				entity.setEnabled(enabled.getAsBoolean());
			}
			final VPackSlice trackSlowQueries = vpack.get("trackSlowQueries");
			if (trackSlowQueries.isBoolean()) {
				entity.setTrackSlowQueries(trackSlowQueries.getAsBoolean());
			}
			final VPackSlice maxSlowQueries = vpack.get("maxSlowQueries");
			if (maxSlowQueries.isNumber()) {
				entity.setMaxSlowQueries(maxSlowQueries.getAsLong());
			}
			final VPackSlice slowQueryThreshold = vpack.get("slowQueryThreshold");
			if (slowQueryThreshold.isNumber()) {
				entity.setSlowQueryThreshold(slowQueryThreshold.getAsLong());
			}
			final VPackSlice maxQueryStringLength = vpack.get("maxQueryStringLength");
			if (maxQueryStringLength.isNumber()) {
				entity.setMaxQueryStringLength(maxQueryStringLength.getAsLong());
			}
			return entity;
		}

	}

	private static VPackSlice getFirstResultAsJsonObject(final VPackSlice obj) throws VPackException {
		final VPackSlice result = obj.get(RESULT);
		if (result.isArray()) {
			return getElementAsJsonObject(result);
		} else if (result.isObject()) {
			return result;
		}
		return null;
	}

	private static VPackSlice getElementAsJsonObject(final VPackSlice arr) {
		if (arr != null && arr.size() > 0) {
			final VPackSlice jsonElement = arr.get(0);
			if (jsonElement.isObject()) {
				return jsonElement;
			}
		}
		return null;
	}

	private static List<VertexEntity<Object>> getVertices(
		final Class<?> vertexClazz,
		final VPackDeserializationContext context,
		final VPackSlice vertices) throws VPackException {
		final List<VertexEntity<Object>> list = new ArrayList<VertexEntity<Object>>();
		if (vertices != null) {
			for (int i = 0, imax = vertices.size(); i < imax; i++) {
				final VPackSlice vertex = vertices.get(i);
				final VertexEntity<Object> ve = getVertex(context, vertex, vertexClazz);
				list.add(ve);
			}
		}
		return list;
	}

	private static VertexEntity<Object> getVertex(
		final VPackDeserializationContext context,
		final VPackSlice vertex,
		final Class<?> vertexClazz) throws VPackException {
		final VertexEntity<Object> ve = deserializeBaseParameter(vertex, new VertexEntity<Object>());
		deserializeDocumentParameter(vertex, ve);
		if (vertexClazz != null) {
			ve.setEntity(context.deserialize(vertex, vertexClazz));
		} else {
			ve.setEntity(context.deserialize(vertex, Object.class));
		}
		return ve;
	}

	private static List<EdgeEntity<Object>> getEdges(
		final Class<?> edgeClazz,
		final VPackDeserializationContext context,
		final VPackSlice edges) throws VPackException {
		final List<EdgeEntity<Object>> list = new ArrayList<EdgeEntity<Object>>();
		if (edges != null) {
			for (int i = 0, imax = edges.size(); i < imax; i++) {
				final VPackSlice edge = edges.get(i);
				final EdgeEntity<Object> ve = deserializeBaseParameter(edge, new EdgeEntity<Object>());
				deserializeDocumentParameter(edge, ve);
				if (edgeClazz != null) {
					ve.setEntity(context.deserialize(edge, edgeClazz));
				} else {
					ve.setEntity(context.deserialize(edge, Object.class));
				}
				list.add(ve);
			}
		}
		return list;
	}
}
