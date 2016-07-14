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
		private final Field field;
		private final boolean serialize;
		private final boolean deserialize;

		private FieldInfo(final String fieldName, final Field field, final boolean serialize,
			final boolean deserialize) {
			super();
			this.fieldName = fieldName;
			this.field = field;
			this.serialize = serialize;
			this.deserialize = deserialize;
		}

		public String getFieldName() {
			return fieldName;
		}

		public Field getField() {
			return field;
		}

		public boolean isSerialize() {
			return serialize;
		}

		public boolean isDeserialize() {
			return deserialize;
		}
	}

	private final Map<Class<?>, Map<String, FieldInfo>> cache;
	private final Comparator<Entry<String, FieldInfo>> fieldComparator;

	public VPackCache() {
		super();
		cache = new ConcurrentHashMap<Class<?>, Map<String, FieldInfo>>();
		fieldComparator = new Comparator<Map.Entry<String, FieldInfo>>() {
			@Override
			public int compare(final Entry<String, FieldInfo> o1, final Entry<String, FieldInfo> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		};
	}

	public FieldInfo getFieldInfo(final Class<?> entityClass, final Field field) {
		return getFields(entityClass).get(field);
	}

	public Map<String, FieldInfo> getFields(final Class<?> entityClass) {
		Map<String, FieldInfo> fields = cache.get(entityClass);
		if (fields == null) {
			fields = new HashMap<String, VPackCache.FieldInfo>();
			Class<?> tmp = entityClass;
			while (tmp != null && tmp != Object.class) {
				final Field[] declaredFields = tmp.getDeclaredFields();
				for (final Field field : declaredFields) {
					if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
						field.setAccessible(true);
						final FieldInfo fieldInfo = createFieldInfo(field);
						fields.put(fieldInfo.getFieldName(), fieldInfo);
					}
				}
				tmp = tmp.getSuperclass();
			}
			fields = sort(fields.entrySet());
			cache.put(entityClass, fields);
		}
		return fields;
	}

	private Map<String, FieldInfo> sort(final Set<Entry<String, FieldInfo>> entrySet) {
		final Map<String, FieldInfo> sorted = new LinkedHashMap<String, VPackCache.FieldInfo>();
		final List<Entry<String, FieldInfo>> tmp = new ArrayList<Entry<String, FieldInfo>>(entrySet);
		Collections.sort(tmp, fieldComparator);
		for (final Entry<String, FieldInfo> entry : tmp) {
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
		return new FieldInfo(fieldName, field, serialize, deserialize);
	}

}
