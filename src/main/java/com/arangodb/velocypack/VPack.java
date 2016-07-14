package com.arangodb.velocypack;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.arangodb.velocypack.VPackBuilder.BuilderOptions;
import com.arangodb.velocypack.exception.VPackException;
import com.arangodb.velocypack.exception.VPackKeyTypeException;
import com.arangodb.velocypack.exception.VPackParserException;
import com.arangodb.velocypack.internal.VPackCache;
import com.arangodb.velocypack.internal.VPackCache.FieldInfo;
import com.arangodb.velocypack.internal.VPackDeserializers;
import com.arangodb.velocypack.internal.VPackInstanceCreators;
import com.arangodb.velocypack.internal.VPackOptionsImpl;
import com.arangodb.velocypack.internal.VPackSerializers;

/**
 * @author Mark - mark@arangodb.com
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class VPack {

	public static interface VPackOptions extends BuilderOptions {

	}

	private static final String ATTR_KEY = "key";
	private static final String ATTR_VALUE = "value";
	private static final Collection<Class<?>> KEY_TYPES;
	static {
		KEY_TYPES = new ArrayList<Class<?>>();
		KEY_TYPES.add(Boolean.class);
		KEY_TYPES.add(Integer.class);
		KEY_TYPES.add(Long.class);
		KEY_TYPES.add(Float.class);
		KEY_TYPES.add(Short.class);
		KEY_TYPES.add(Double.class);
		KEY_TYPES.add(Number.class);
		KEY_TYPES.add(BigInteger.class);
		KEY_TYPES.add(BigDecimal.class);
		KEY_TYPES.add(String.class);
		KEY_TYPES.add(Character.class);
		KEY_TYPES.add(Enum.class);
	}

	private final Map<Class<?>, VPackSerializer<?>> serializers;
	private final Map<Class<?>, VPackDeserializer<?>> deserializers;
	private final Map<Class<?>, VPackInstanceCreator<?>> instanceCreators;
	private final VPackOptions options;

	private final VPackCache cache;
	private final VPackSerializationContext serializationContext;
	private final VPackDeserializationContext deserializationContext;

	public VPack() {
		this(new VPackOptionsImpl());
	}

	public VPack(final VPackOptions options) {
		super();
		this.options = options;
		serializers = new HashMap<Class<?>, VPackSerializer<?>>();
		deserializers = new HashMap<Class<?>, VPackDeserializer<?>>();
		instanceCreators = new HashMap<Class<?>, VPackInstanceCreator<?>>();
		cache = new VPackCache();
		serializationContext = new VPackSerializationContext() {
			@Override
			public void serialize(final VPackBuilder builder, final Object entity) throws VPackParserException {
				try {
					serializeObject(null, entity, builder);
				} catch (final Exception e) {
					throw new VPackParserException(e);
				}
			}

			@Override
			public void serialize(final VPackBuilder builder, final String attribute, final Object entity)
					throws VPackParserException {
				try {
					serializeObject(attribute, entity, builder);
				} catch (final Exception e) {
					throw new VPackParserException(e);
				}
			}
		};
		deserializationContext = new VPackDeserializationContext() {
			@Override
			public <T> T deserialize(final VPackSlice vpack, final Class<T> type) throws VPackParserException {
				return VPack.this.deserialize(vpack, type);
			}
		};
		instanceCreators.put(Collection.class, VPackInstanceCreators.COLLECTION);
		instanceCreators.put(List.class, VPackInstanceCreators.LIST);
		instanceCreators.put(Set.class, VPackInstanceCreators.SET);
		instanceCreators.put(Map.class, VPackInstanceCreators.MAP);

		serializers.put(String.class, VPackSerializers.STRING);
		serializers.put(Boolean.class, VPackSerializers.BOOLEAN);
		serializers.put(boolean.class, VPackSerializers.BOOLEAN);
		serializers.put(Integer.class, VPackSerializers.INTEGER);
		serializers.put(int.class, VPackSerializers.INTEGER);
		serializers.put(Long.class, VPackSerializers.LONG);
		serializers.put(long.class, VPackSerializers.LONG);
		serializers.put(Short.class, VPackSerializers.SHORT);
		serializers.put(short.class, VPackSerializers.SHORT);
		serializers.put(Double.class, VPackSerializers.DOUBLE);
		serializers.put(double.class, VPackSerializers.DOUBLE);
		serializers.put(Float.class, VPackSerializers.FLOAT);
		serializers.put(float.class, VPackSerializers.FLOAT);
		serializers.put(BigInteger.class, VPackSerializers.BIG_INTEGER);
		serializers.put(BigDecimal.class, VPackSerializers.BIG_DECIMAL);
		serializers.put(Number.class, VPackSerializers.NUMBER);
		serializers.put(Character.class, VPackSerializers.CHARACTER);
		serializers.put(char.class, VPackSerializers.CHARACTER);

		deserializers.put(String.class, VPackDeserializers.STRING);
		deserializers.put(Boolean.class, VPackDeserializers.BOOLEAN);
		deserializers.put(boolean.class, VPackDeserializers.BOOLEAN);
		deserializers.put(Integer.class, VPackDeserializers.INTEGER);
		deserializers.put(int.class, VPackDeserializers.INTEGER);
		deserializers.put(Long.class, VPackDeserializers.LONG);
		deserializers.put(long.class, VPackDeserializers.LONG);
		deserializers.put(Short.class, VPackDeserializers.SHORT);
		deserializers.put(short.class, VPackDeserializers.SHORT);
		deserializers.put(Double.class, VPackDeserializers.DOUBLE);
		deserializers.put(double.class, VPackDeserializers.DOUBLE);
		deserializers.put(Float.class, VPackDeserializers.FLOAT);
		deserializers.put(float.class, VPackDeserializers.FLOAT);
		deserializers.put(BigInteger.class, VPackDeserializers.BIG_INTEGER);
		deserializers.put(BigDecimal.class, VPackDeserializers.BIG_DECIMAL);
		deserializers.put(Number.class, VPackDeserializers.NUMBER);
		deserializers.put(Character.class, VPackDeserializers.CHARACTER);
		deserializers.put(char.class, VPackDeserializers.CHARACTER);
	}

	public VPackOptions getOptions() {
		return options;
	}

	public <T> void registerSerializer(final Class<T> clazz, final VPackSerializer<T> serializer) {
		serializers.put(clazz, serializer);
	}

	public <T> void registerDeserializer(final Class<T> clazz, final VPackDeserializer<T> deserializer) {
		deserializers.put(clazz, deserializer);
	}

	public <T> void regitserInstanceCreator(final Class<T> clazz, final VPackInstanceCreator<T> creator) {
		instanceCreators.put(clazz, creator);
	}

	public <T> T deserialize(final VPackSlice vpack, final Class<T> type) throws VPackParserException {
		final T entity;
		try {
			entity = deserializeObject(vpack, type);
		} catch (final Exception e) {
			throw new VPackParserException(e);
		}
		return entity;
	}

	private <T> T deserializeObject(final VPackSlice vpack, final Class<T> type) throws InstantiationException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException, VPackException {
		final T entity;
		final VPackDeserializer<?> deserializer = deserializers.get(type);
		if (deserializer != null) {
			entity = ((VPackDeserializer<T>) deserializer).deserialize(vpack, deserializationContext);
		} else {
			entity = createInstance(type);
			deserializeFields(entity, vpack);
		}
		return entity;
	}

	private <T> T createInstance(final Class<T> type) throws InstantiationException, IllegalAccessException {
		final T entity;
		final VPackInstanceCreator<?> creator = instanceCreators.get(type);
		if (creator != null) {
			entity = (T) creator.createInstance();
		} else {
			entity = type.newInstance();
		}
		return entity;
	}

	private void deserializeFields(final Object entity, final VPackSlice vpack) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException, InstantiationException, VPackException {
		final Map<String, FieldInfo> fields = cache.getFields(entity.getClass());
		for (final FieldInfo fieldInfo : fields.values()) {
			if (fieldInfo.isDeserialize()) {
				deserializeField(vpack, entity, fieldInfo);
			}
		}
	}

	private void deserializeField(final VPackSlice vpack, final Object entity, final FieldInfo fieldInfo)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException,
			VPackException {
		final VPackSlice attr = vpack.get(fieldInfo.getFieldName());
		if (!attr.isNone()) {
			final Field field = fieldInfo.getField();
			final Object value = getValue(attr, field, field.getType());
			field.set(entity, value);
		}
	}

	private Class<?> getComponentType(final Field field, final Class<?> type, final int i) {
		Class<?> result;
		final Class<?> componentType = type.getComponentType();
		if (componentType != null) {
			result = componentType;
		} else {
			final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
			final Type argType = genericType.getActualTypeArguments()[i];
			result = (Class<?>) (ParameterizedType.class.isAssignableFrom(argType.getClass())
					? ParameterizedType.class.cast(argType).getRawType() : argType);
		}
		return result;
	}

	private <T> Object getValue(final VPackSlice vpack, final Field field, final Class<T> type)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
			VPackException {
		final Object value;
		if (vpack.isNull()) {
			value = null;
		} else {
			final VPackDeserializer<?> deserializer = deserializers.get(type);
			if (deserializer != null) {
				value = ((VPackDeserializer<Object>) deserializer).deserialize(vpack, deserializationContext);
			} else if (type.isArray()) {
				value = deserializeArray(vpack, field, type);
			} else if (type.isEnum()) {
				value = Enum.valueOf((Class<? extends Enum>) type, vpack.getAsString());
			} else if (Collection.class.isAssignableFrom(type)) {
				value = deserializeCollection(vpack, field, type);
			} else if (Map.class.isAssignableFrom(type)) {
				value = deserializeMap(vpack, field, type);
			} else {
				value = deserializeObject(vpack, type);
			}
		}
		return value;
	}

	private <T> Object deserializeArray(final VPackSlice vpack, final Field field, final Class<T> type)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
			ArrayIndexOutOfBoundsException, IllegalArgumentException, VPackException {
		final int length = (int) vpack.getLength();
		final Class<?> componentType = getComponentType(field, type, 0);
		final Object value = Array.newInstance(componentType, length);
		for (int i = 0; i < length; i++) {
			Array.set(value, i, getValue(vpack.at(i), null, componentType));
		}
		return value;
	}

	private <T> Object deserializeCollection(final VPackSlice vpack, final Field field, final Class<T> type)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
			VPackException {
		final Collection value = (Collection) createInstance(type);
		final long length = vpack.getLength();
		if (length > 0) {
			final Class<?> componentType = getComponentType(field, type, 0);
			for (int i = 0; i < length; i++) {
				value.add(getValue(vpack.at(i), null, componentType));
			}
		}
		return value;
	}

	private <T> Object deserializeMap(final VPackSlice vpack, final Field field, final Class<T> type)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
			VPackException {
		final int length = (int) vpack.getLength();
		final Map value = (Map) createInstance(type);
		if (length > 0) {
			final Class<?> keyType = getComponentType(field, type, 0);
			final Class<?> valueType = getComponentType(field, type, 1);
			if (isStringableKeyType(keyType)) {
				for (int i = 0; i < vpack.getLength(); i++) {
					value.put(getKeyfromString(vpack.keyAt(i).makeKey().getAsString(), keyType),
						getValue(vpack.valueAt(i), null, valueType));
				}
			} else {
				for (int i = 0; i < vpack.getLength(); i++) {
					final VPackSlice entry = vpack.at(i);
					final Object mapKey = getValue(entry.get(ATTR_KEY), null, keyType);
					final Object mapValue = getValue(entry.get(ATTR_VALUE), null, valueType);
					value.put(mapKey, mapValue);
				}
			}
		}
		return value;
	}

	private Object getKeyfromString(final String key, final Class<?> type) throws VPackKeyTypeException {
		final Object result;
		if (type == String.class) {
			result = key;
		} else if (type == Integer.class) {
			result = Integer.valueOf(key);
		} else if (type == Long.class) {
			result = Long.valueOf(key);
		} else if (type == Float.class) {
			result = Float.valueOf(key);
		} else if (type == Short.class) {
			result = Short.valueOf(key);
		} else if (type == Double.class || type == Number.class) {
			result = Double.valueOf(key);
		} else if (type == BigInteger.class) {
			result = new BigInteger(key);
		} else if (type == BigDecimal.class) {
			result = new BigDecimal(key);
		} else if (type == Character.class && key.length() == 1) {
			result = key.charAt(0);
		} else if (Enum.class.isAssignableFrom(type)) {
			final Class<? extends Enum> enumType = (Class<? extends Enum>) type;
			result = Enum.valueOf(enumType, key);
		} else if (type == Boolean.class) {
			result = Boolean.valueOf(key);
		} else {
			throw new VPackKeyTypeException(String.format("can not convert key: %s in type: %s", key, type.getName()));
		}
		return result;
	}

	public VPackSlice serialize(final Object entity) throws VPackParserException {
		final VPackBuilder builder = new VPackBuilder(options);
		try {
			serializeObject(null, entity, builder);
		} catch (final Exception e) {
			throw new VPackParserException(e);
		}
		return builder.slice();
	}

	private void serializeObject(final String name, final Object entity, final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackException {

		builder.add(name, new Value(ValueType.OBJECT));
		final VPackSerializer<?> serializer = serializers.get(entity.getClass());
		if (serializer != null) {
			((VPackSerializer<Object>) serializer).serialize(builder, name, entity, serializationContext);
		} else {
			serializeFields(entity, builder);
		}
		builder.close(false);
	}

	private void serializeFields(final Object entity, final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackException {
		final Map<String, FieldInfo> fields = cache.getFields(entity.getClass());
		for (final FieldInfo fieldInfo : fields.values()) {
			if (fieldInfo.isSerialize()) {
				serializeField(entity, builder, fieldInfo);
			}
		}
	}

	private void serializeField(final Object entity, final VPackBuilder builder, final FieldInfo fieldInfo)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackException {

		final String fieldName = fieldInfo.getFieldName();
		final Field field = fieldInfo.getField();
		final Class<?> type = field.getType();
		final Object value = field.get(entity);
		addValue(field, fieldName, type, value, builder);
	}

	private void addValue(
		final Field field,
		final String name,
		final Class<?> type,
		final Object value,
		final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackException {

		if (value == null) {
			builder.add(name, new Value(ValueType.NULL));
		} else {
			final VPackSerializer<?> serializer = serializers.get(type);
			if (serializer != null) {
				((VPackSerializer<Object>) serializer).serialize(builder, name, value, serializationContext);
			} else if (type.isArray()) {
				serializeArray(name, value, builder);
			} else if (type.isEnum()) {
				builder.add(name, new Value(Enum.class.cast(value).name()));
			} else if (Iterable.class.isAssignableFrom(type)) {
				serializeIterable(name, value, builder);
			} else if (Map.class.isAssignableFrom(type)) {
				serializeMap(field, name, type, value, builder);
			} else {
				serializeObject(name, value, builder);
			}
		}

	}

	private void serializeArray(final String name, final Object value, final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackException {
		builder.add(name, new Value(ValueType.ARRAY));
		for (int i = 0; i < Array.getLength(value); i++) {
			final Object element = Array.get(value, i);
			addValue(null, null, element.getClass(), element, builder);
		}
		builder.close();
	}

	private void serializeIterable(final String name, final Object value, final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackException {
		builder.add(name, new Value(ValueType.ARRAY));
		for (final Iterator iterator = Iterable.class.cast(value).iterator(); iterator.hasNext();) {
			final Object element = iterator.next();
			addValue(null, null, element.getClass(), element, builder);
		}
		builder.close();
	}

	private void serializeMap(
		final Field field,
		final String name,
		final Class<?> type,
		final Object value,
		final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackException {
		final Map map = Map.class.cast(value);
		if (map.size() > 0) {
			final Class<?> keyType = getComponentType(field, type, 0);
			if (isStringableKeyType(keyType)) {
				builder.add(name, new Value(ValueType.OBJECT));
				final Set<Entry<?, ?>> entrySet = map.entrySet();
				for (final Entry<?, ?> entry : entrySet) {
					addValue(null, keyToString(entry.getKey()), entry.getValue().getClass(), entry.getValue(), builder);
				}
				builder.close();
			} else {
				builder.add(name, new Value(ValueType.ARRAY));
				final Set<Entry<?, ?>> entrySet = map.entrySet();
				for (final Entry<?, ?> entry : entrySet) {
					builder.add(null, new Value(ValueType.OBJECT));
					addValue(null, ATTR_KEY, entry.getKey().getClass(), entry.getKey(), builder);
					addValue(null, ATTR_VALUE, entry.getValue().getClass(), entry.getValue(), builder);
					builder.close();
				}
				builder.close();
			}
		} else {
			builder.add(name, new Value(ValueType.OBJECT));
			builder.close();
		}
	}

	private boolean isStringableKeyType(final Class<?> type) {
		return KEY_TYPES.contains(type) || Enum.class.isAssignableFrom(type);
	}

	private String keyToString(final Object key) {
		return Enum.class.isAssignableFrom(key.getClass()) ? Enum.class.cast(key).name() : key.toString();
	}

}
