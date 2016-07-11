package com.arangodb.velocypack.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.arangodb.velocypack.annotations.Expose;
import com.arangodb.velocypack.annotations.SerializedName;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackCache {

	public static class FieldInfo {
		private final String fieldName;
		private final boolean serialize;
		private final boolean deserialize;

		public FieldInfo(final String fieldName, final boolean serialize, final boolean deserialize) {
			super();
			this.fieldName = fieldName;
			this.serialize = serialize;
			this.deserialize = deserialize;
		}

		public String getFieldName() {
			return fieldName;
		}

		public boolean isSerialize() {
			return serialize;
		}

		public boolean isDeserialize() {
			return deserialize;
		}
	}

	private final Map<Class<?>, Map<Field, FieldInfo>> cache;
	private final Comparator<Entry<Field, FieldInfo>> fieldComparator;

	public VPackCache() {
		super();
		cache = new ConcurrentHashMap<Class<?>, Map<Field, FieldInfo>>();
		fieldComparator = new Comparator<Entry<Field, FieldInfo>>() {
			@Override
			public int compare(final Entry<Field, FieldInfo> o1, final Entry<Field, FieldInfo> o2) {
				return o1.getValue().getFieldName().compareTo(o2.getValue().getFieldName());
			}
		};
	}

	public FieldInfo getFieldInfo(final Class<?> entityClass, final Field field) {
		return getFields(entityClass).get(field);
	}

	public Map<Field, FieldInfo> getFields(final Class<?> entityClass) {
		Map<Field, FieldInfo> fields = cache.get(entityClass);
		if (fields == null) {
			fields = new HashMap<Field, VPackCache.FieldInfo>();
			Class<?> tmp = entityClass;
			while (tmp != null && tmp != Object.class) {
				final Field[] declaredFields = tmp.getDeclaredFields();
				for (final Field field : declaredFields) {
					if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
						field.setAccessible(true);
						fields.put(field, createFieldInfo(field));
					}
				}
				tmp = tmp.getSuperclass();
			}
			fields = sort(fields.entrySet());
			cache.put(entityClass, fields);
		}
		return fields;
	}

	private Map<Field, FieldInfo> sort(final Set<Entry<Field, FieldInfo>> entrySet) {
		final Map<Field, FieldInfo> sorted = new LinkedHashMap<Field, VPackCache.FieldInfo>();
		final List<Entry<Field, FieldInfo>> tmp = new ArrayList<Entry<Field, FieldInfo>>(entrySet);
		Collections.sort(tmp, fieldComparator);
		for (final Entry<Field, FieldInfo> entry : tmp) {
			sorted.put(entry.getKey(), entry.getValue());
		}
		return sorted;
	}

	private FieldInfo createFieldInfo(final Field field) {
		final SerializedName annotationName = field.getAnnotation(SerializedName.class);
		final String fieldName = annotationName != null ? annotationName.value() : field.getName();
		final Expose expose = field.getAnnotation(Expose.class);
		final boolean serialize = expose != null ? expose.serialize() : true;
		final boolean deserialize = expose != null ? expose.deserialize() : true;
		return new FieldInfo(fieldName, serialize, deserialize);
	}

}
