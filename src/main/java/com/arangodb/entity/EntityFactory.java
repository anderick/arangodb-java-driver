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

import java.util.Collection;
import java.util.Map;

import com.arangodb.ArangoException;
import com.arangodb.entity.CollectionEntity.Figures;
import com.arangodb.entity.marker.VertexEntity;
import com.arangodb.velocypack.VPack;
import com.arangodb.velocypack.VPackDeserializer;
import com.arangodb.velocypack.VPackInstanceCreator;
import com.arangodb.velocypack.VPackParser;
import com.arangodb.velocypack.VPackSerializer;
import com.arangodb.velocypack.VPackSlice;
import com.arangodb.velocypack.exception.VPackParserException;

/**
 * Entity factory , internally used.
 *
 * @author tamtam180 - kirscheless at gmail.com
 */
public class EntityFactory {

	private static VPack vpack = new VPack();

	private EntityFactory() {
		// this is a helper class
	}

	static {
		configure();
	}

	public static void configure() {
		vpack.registerSerializer(CollectionStatus.class, new CollectionStatusTypeAdapter())
				.registerDeserializer(CollectionStatus.class, new CollectionStatusTypeAdapter())
				.registerDeserializer(CollectionEntity.class, new EntityDeserializers.CollectionEntityDeserializer())
				.registerDeserializer(CollectionsEntity.class, new EntityDeserializers.CollectionsEntityDeserializer())
				.registerDeserializer(DocumentEntity.class, new EntityDeserializers.DocumentEntityDeserializer())
				.registerDeserializer(DocumentsEntity.class, new EntityDeserializers.DocumentsEntityDeserializer())
				.registerDeserializer(AqlFunctionsEntity.class,
					new EntityDeserializers.AqlfunctionsEntityDeserializer())
				.registerDeserializer(JobsEntity.class, new EntityDeserializers.JobsEntityDeserializer())
				.registerDeserializer(ArangoVersion.class, new EntityDeserializers.VersionDeserializer())
				.registerDeserializer(ArangoUnixTime.class, new EntityDeserializers.ArangoUnixTimeDeserializer())
				.registerDeserializer(DefaultEntity.class, new EntityDeserializers.DefaultEntityDeserializer())
				.registerDeserializer(Figures.class, new EntityDeserializers.FiguresDeserializer())
				.registerDeserializer(CursorEntity.class, new EntityDeserializers.CursorEntityDeserializer())
				.registerDeserializer(IndexEntity.class, new EntityDeserializers.IndexEntityDeserializer())
				.registerDeserializer(IndexesEntity.class, new EntityDeserializers.IndexesEntityDeserializer())
				.registerDeserializer(ScalarExampleEntity.class,
					new EntityDeserializers.ScalarExampleEntityDeserializer())
				.registerDeserializer(SimpleByResultEntity.class,
					new EntityDeserializers.SimpleByResultEntityDeserializer())
				.registerDeserializer(TransactionResultEntity.class,
					new EntityDeserializers.TransactionResultEntityDeserializer())
				.registerDeserializer(AdminLogEntity.class, new EntityDeserializers.AdminLogEntryEntityDeserializer())
				.registerDeserializer(StatisticsEntity.class, new EntityDeserializers.StatisticsEntityDeserializer())
				.registerDeserializer(StatisticsDescriptionEntity.class,
					new EntityDeserializers.StatisticsDescriptionEntityDeserializer())
				.registerDeserializer(UserEntity.class, new EntityDeserializers.UserEntityDeserializer())
				.registerDeserializer(ImportResultEntity.class,
					new EntityDeserializers.ImportResultEntityDeserializer())
				.registerDeserializer(DatabaseEntity.class, new EntityDeserializers.DatabaseEntityDeserializer())
				.registerDeserializer(StringsResultEntity.class,
					new EntityDeserializers.StringsResultEntityDeserializer())
				.registerDeserializer(BooleanResultEntity.class,
					new EntityDeserializers.BooleanResultEntityDeserializer())
				.registerDeserializer(Endpoint.class, new EntityDeserializers.EndpointDeserializer())
				.registerDeserializer(DocumentResultEntity.class,
					new EntityDeserializers.DocumentResultEntityDeserializer())
				.registerDeserializer(CollectionKeyOption.class,
					new EntityDeserializers.CollectionKeyOptionDeserializer())
				.registerDeserializer(ReplicationInventoryEntity.class,
					new EntityDeserializers.ReplicationInventoryEntityDeserializer())
				.registerDeserializer(ReplicationDumpRecord.class,
					new EntityDeserializers.ReplicationDumpRecordDeserializer())
				.registerDeserializer(ReplicationSyncEntity.class,
					new EntityDeserializers.ReplicationSyncEntityDeserializer())
				.registerDeserializer(MapAsEntity.class, new EntityDeserializers.MapAsEntityDeserializer())
				.registerDeserializer(ReplicationLoggerConfigEntity.class,
					new EntityDeserializers.ReplicationLoggerConfigEntityDeserializer())
				.registerDeserializer(ReplicationApplierConfigEntity.class,
					new EntityDeserializers.ReplicationApplierConfigEntityDeserializer())
				.registerDeserializer(ReplicationApplierState.class,
					new EntityDeserializers.ReplicationApplierStateDeserializer())
				.registerDeserializer(ReplicationApplierStateEntity.class,
					new EntityDeserializers.ReplicationApplierStateEntityDeserializer())
				.registerDeserializer(ReplicationLoggerStateEntity.class,
					new EntityDeserializers.ReplicationLoggerStateEntityDeserializer())
				.registerDeserializer(ReplicationLoggerStateEntity.Client.class,
					new EntityDeserializers.ReplicationLoggerStateEntityClientDeserializer())
				.registerDeserializer(GraphEntity.class, new EntityDeserializers.GraphEntityDeserializer())
				.registerDeserializer(GraphsEntity.class, new EntityDeserializers.GraphsEntityDeserializer())
				.registerDeserializer(DeletedEntity.class, new EntityDeserializers.DeleteEntityDeserializer())
				.registerDeserializer(VertexEntity.class, new EntityDeserializers.VertexEntityDeserializer())
				.registerDeserializer(EdgeEntity.class, new EntityDeserializers.EdgeEntityDeserializer())
				.registerDeserializer(TraversalEntity.class, new EntityDeserializers.TraversalEntityDeserializer())
				.registerDeserializer(ShortestPathEntity.class,
					new EntityDeserializers.ShortestPathEntityDeserializer())
				.registerDeserializer(QueryCachePropertiesEntity.class,
					new EntityDeserializers.QueryCachePropertiesEntityDeserializer())
				.registerDeserializer(QueriesResultEntity.class,
					new EntityDeserializers.QueriesResultEntityDeserializer())
				.registerDeserializer(QueryTrackingPropertiesEntity.class,
					new EntityDeserializers.QueryTrackingPropertiesEntityDeserializer());

		// return new GsonBuilder().addSerializationExclusionStrategy(new
		// ExcludeExclusionStrategy(true))
		// .addDeserializationExclusionStrategy(new
		// ExcludeExclusionStrategy(false))
		// .setFieldNamingStrategy(new ArangoFieldNamingStrategy())
		// .registerTypeAdapter(CollectionStatus.class, new
		// CollectionStatusTypeAdapter())
		// .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	}

	public static <T> VPack registerSerializer(final Class<T> clazz, final VPackSerializer<T> serializer) {
		return vpack.registerSerializer(clazz, serializer);
	}

	public static <T> VPack registerDeserializer(final Class<T> clazz, final VPackDeserializer<T> deserializer) {
		return vpack.registerDeserializer(clazz, deserializer);
	}

	public static <T> VPack regitserInstanceCreator(final Class<T> clazz, final VPackInstanceCreator<T> creator) {
		return vpack.regitserInstanceCreator(clazz, creator);
	}

	public static <T> T createEntity(final VPackSlice vpack, final Class<T> type) throws ArangoException {
		try {
			return EntityFactory.vpack.deserialize(vpack, type);
		} catch (final VPackParserException e) {
			throw new ArangoException(e);
		}
	}

	public static <T extends Collection<C>, C> T createEntity(
		final VPackSlice vpack,
		final Class<T> type,
		final Class<C> contentType) throws ArangoException {
		try {
			return EntityFactory.vpack.deserialize(vpack, type, contentType);
		} catch (final VPackParserException e) {
			throw new ArangoException(e);
		}
	}

	public static <T extends Map<K, C>, K, C> T createEntity(
		final VPackSlice vpack,
		final Class<T> type,
		final Class<K> keyType,
		final Class<C> contentType) throws ArangoException {
		try {
			return EntityFactory.vpack.deserialize(vpack, type, keyType, contentType);
		} catch (final VPackParserException e) {
			throw new ArangoException(e);
		}
	}

	public static <T> VPackSlice toVPack(final T obj) throws ArangoException {
		try {
			return EntityFactory.vpack.serialize(obj);
		} catch (final VPackParserException e) {
			throw new ArangoException(e);
		}
	}

	public static <T extends Map<?, ?>> VPackSlice toVPack(final T obj) throws ArangoException {
		return toVPack(obj, String.class);
	}

	public static <T extends Map<?, ?>> VPackSlice toVPack(final T obj, final Class<?> keyType) throws ArangoException {
		try {
			return EntityFactory.vpack.serialize(obj, keyType);
		} catch (final VPackParserException e) {
			throw new ArangoException(e);
		}
	}

	public static String toJson(final VPackSlice vpack) {
		return VPackParser.toJson(vpack, false);
	}

	public static String toJson(final VPackSlice vpack, final boolean includeNullValue) {
		return VPackParser.toJson(vpack, includeNullValue);
	}
	//
	// public static <T> String toJsonString(final T obj) throws
	// VPackParserException {
	// return toJsonString(obj, false);
	// }

	public static VPackSlice toImportHeaderValues(final Collection<? extends Collection<?>> headerValues)
			throws ArangoException {
		try {
			return EntityFactory.vpack.serialize(headerValues);
		} catch (final VPackParserException e) {
			throw new ArangoException(e);
		}
		// final StringWriter writer = new StringWriter();
		// for (final Collection<?> array : headerValues) {
		// gson.toJson(array, writer);
		// writer.write('\n');
		// }
		// writer.flush();
		// return writer.toString();
	}

	// public static <T> String toJsonString(final T obj, final boolean
	// includeNullValue) throws VPackParserException {
	// // TODO includeNullValue
	// return VPackParser.toJson(EntityFactory.vpack.serialize(obj));
	//
	// // if (obj != null && obj.getClass().equals(BaseDocument.class)) {
	// // final String tmp = includeNullValue ? gsonNull.toJson(obj) :
	// // gson.toJson(obj);
	// // final JsonParser jsonParser = new JsonParser();
	// // final JsonElement jsonElement = jsonParser.parse(tmp);
	// // final JsonObject jsonObject = jsonElement.getAsJsonObject();
	// // final JsonObject result = jsonObject.getAsJsonObject("properties");
	// // final JsonElement keyObject = jsonObject.get("_key");
	// // if (keyObject != null && keyObject.getClass() != JsonNull.class) {
	// // result.add("_key", jsonObject.get("_key"));
	// // }
	// // final JsonElement handleObject = jsonObject.get("_id");
	// // if (handleObject != null && handleObject.getClass() !=
	// // JsonNull.class) {
	// // result.add("_id", jsonObject.get("_id"));
	// // }
	// // // JsonElement revisionValue = jsonObject.get("documentRevision");
	// // // result.add("_rev", revisionValue);
	// // return result.toString();
	// // }
	// // return includeNullValue ? gsonNull.toJson(obj) : gson.toJson(obj);
	// }

	// /**
	// * @author tamtam180 - kirscheless at gmail.com
	// * @since 1.4.0
	// */
	// private static class ExcludeExclusionStrategy implements
	// ExclusionStrategy {
	// private final boolean serialize;
	//
	// public ExcludeExclusionStrategy(final boolean serialize) {
	// this.serialize = serialize;
	// }
	//
	// @Override
	// public boolean shouldSkipField(final FieldAttributes f) {
	// final Exclude annotation = f.getAnnotation(Exclude.class);
	// if (annotation != null && (serialize ? annotation.serialize() :
	// annotation.deserialize())) {
	// return true;
	// }
	// return false;
	// }
	//
	// @Override
	// public boolean shouldSkipClass(final Class<?> clazz) {
	// return false;
	// }
	// }
	//
	// private static class ArangoFieldNamingStrategy implements
	// FieldNamingStrategy {
	// private static final String KEY = "_key";
	//
	// @Override
	// public String translateName(final Field f) {
	// final DocumentKey key = f.getAnnotation(DocumentKey.class);
	// if (key == null) {
	// return FieldNamingPolicy.IDENTITY.translateName(f);
	// }
	// return KEY;
	// }
	// }
}
