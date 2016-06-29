package com.arangodb.velocypack;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.arangodb.velocypack.VPackBuilder.BuilderOptions;
import com.arangodb.velocypack.VPackSlice.SliceOptions;
import com.arangodb.velocypack.annotations.SerializedName;
import com.arangodb.velocypack.defaults.VPackDefaultOptions;
import com.arangodb.velocypack.defaults.VPackDefautInstanceCreators;
import com.arangodb.velocypack.exception.VPackBuilderException;
import com.arangodb.velocypack.exception.VPackKeyTypeException;
import com.arangodb.velocypack.exception.VPackParserException;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class VPack {

	public static interface VPackOptions extends SliceOptions, BuilderOptions {

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

	private final Map<Class<?>, Collection<Field>> fieldCache;
	private final Map<Field, String> fieldNameCache;

	public VPack() {
		this(new VPackDefaultOptions());
	}

	public VPack(final VPackOptions options) {
		super();
		this.options = options;
		serializers = new HashMap<Class<?>, VPackSerializer<?>>();
		deserializers = new HashMap<Class<?>, VPackDeserializer<?>>();
		instanceCreators = new HashMap<Class<?>, VPackInstanceCreator<?>>();
		VPackDefautInstanceCreators.registerInstanceCreators(this);
		fieldCache = new HashMap<Class<?>, Collection<Field>>();
		fieldNameCache = new HashMap<Field, String>();
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

	private void setSliceOptions(final VPackSlice slice) {
		slice.getOptions().setKeyTranslator(options.getKeyTranslator());
	}

	public <T> T deserialize(final VPackSlice vpack, final Class<T> type) throws VPackParserException {
		setSliceOptions(vpack);
		final T entity;
		try {
			entity = deserializeInternal(vpack, type);
		} catch (final Exception e) {
			throw new VPackParserException(e);
		}
		return entity;
	}

	private <T> T deserializeInternal(final VPackSlice vpack, final Class<T> type) throws InstantiationException,
			IllegalAccessException, NoSuchMethodException, InvocationTargetException, VPackKeyTypeException {
		final T entity;
		final VPackDeserializer<T> deserializer = (VPackDeserializer<T>) deserializers.get(type);
		if (deserializer != null) {
			entity = deserializer.deserialize(vpack);
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

	private void deserializeFields(final Object entity, final VPackSlice vpack) throws VPackKeyTypeException,
			NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		final Collection<Field> fields = getDeclaredFields(entity);
		for (final Field field : fields) {
			deserializeField(vpack, entity, field);
		}
	}

	private void deserializeField(final VPackSlice vpack, final Object entity, final Field field)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException,
			VPackKeyTypeException {
		final VPackSlice attr = vpack.get(field.getName());
		if (!attr.isNone()) {
			final Object value = getValue(attr, field, field.getType());
			setEntityValue(entity, field, value);
		}
	}

	private Class<?> getComponentType(final Field field, final Class<?> type) {
		return getComponentType(field, type, 0);
	}

	private Class<?> getComponentKeyType(final Field field, final Class<?> type) {
		return getComponentType(field, type, 0);
	}

	private Class<?> getComponentValueType(final Field field, final Class<?> type) {
		return getComponentType(field, type, 1);
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
			VPackKeyTypeException {
		final Object value;
		if (vpack.isNull()) {
			value = null;
		} else if (type == Boolean.class || type == boolean.class) {
			value = vpack.getAsBoolean();
		} else if (type == Integer.class || type == int.class) {
			value = vpack.getAsInt();
		} else if (type == Long.class || type == long.class) {
			value = vpack.getAsLong();
		} else if (type == Float.class || type == float.class) {
			value = vpack.getAsFloat();
		} else if (type == Short.class || type == short.class) {
			value = vpack.getAsShort();
		} else if (type == Double.class || type == double.class) {
			value = vpack.getAsDouble();
		} else if (type == BigInteger.class) {
			value = vpack.getAsBigInteger();
		} else if (type == BigDecimal.class) {
			value = vpack.getAsBigDecimal();
		} else if (type == String.class) {
			value = vpack.getAsString();
		} else if (type == Character.class || type == char.class) {
			value = vpack.getAsChar();
		} else if (type.isArray()) {
			value = getArray(vpack, field, type);
		} else if (type.isEnum()) {
			final Class<? extends Enum> enumType = (Class<? extends Enum>) type;
			value = Enum.valueOf(enumType, vpack.getAsString());
		} else if (Collection.class.isAssignableFrom(type)) {
			value = getCollection(vpack, field, type);
		} else if (Map.class.isAssignableFrom(type)) {
			value = getMap(vpack, field, type);
		} else {
			value = deserializeInternal(vpack, type);
		}
		return value;
	}

	private <T> Object getArray(final VPackSlice vpack, final Field field, final Class<T> type)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
			VPackKeyTypeException {
		final int length = (int) vpack.getLength();
		final Class<?> componentType = getComponentType(field, type);
		final Object value = Array.newInstance(componentType, length);
		for (int i = 0; i < length; i++) {
			Array.set(value, i, getValue(vpack.at(i), null, componentType));
		}
		return value;
	}

	private <T> Object getCollection(final VPackSlice vpack, final Field field, final Class<T> type)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException,
			VPackKeyTypeException {
		final Collection value = (Collection) createInstance(type);
		final long length = vpack.getLength();
		if (length > 0) {
			final Class<?> componentType = getComponentType(field, type);
			for (int i = 0; i < length; i++) {
				value.add(getValue(vpack.at(i), null, componentType));
			}
		}
		return value;
	}

	private <T> Object getMap(final VPackSlice vpack, final Field field, final Class<T> type)
			throws VPackKeyTypeException, InstantiationException, IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		final int length = (int) vpack.getLength();
		final Map value = new HashMap();
		if (length > 0) {
			final Class<?> keyType = getComponentKeyType(field, type);
			final Class<?> valueType = getComponentValueType(field, type);
			if (isStringableKeyType(keyType)) {
				for (int i = 0; i < vpack.getLength(); i++) {
					value.put(getKeyfromString(getKeyAsString(vpack.keyAt(i)), keyType),
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

	private String getKeyAsString(final VPackSlice key) throws VPackKeyTypeException {
		final String result;
		if (key.isString()) {
			result = key.getAsString();
		} else if (key.isInteger()) {
			result = options.getKeyTranslator().fromKey(key.getAsInt());
		} else {
			throw new VPackKeyTypeException("Expecting type String oder Integer for key");
		}
		return result;
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
			serializeInternal(null, entity, builder);
		} catch (final Exception e) {
			throw new VPackParserException(e);
		}
		return builder.slice();
	}

	private void serializeInternal(final String name, final Object entity, final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackBuilderException {

		add(name, new Value(ValueType.OBJECT), builder);
		final VPackSerializer<Object> serializer = (VPackSerializer<Object>) serializers.get(entity.getClass());
		if (serializer != null) {
			serializer.serialize(builder, entity);
		} else {
			serializeFields(entity, builder);
		}
		builder.close();
	}

	private void serializeFields(final Object entity, final VPackBuilder builder)
			throws VPackBuilderException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Collection<Field> fields = getDeclaredFields(entity);
		for (final Field field : fields) {
			serializeField(entity, builder, field);
		}
	}

	private Collection<Field> getDeclaredFields(final Object entity) {

		Collection<Field> fields = fieldCache.get(entity.getClass());
		if (fields == null) {
			fields = new ArrayList<Field>();
			Class<?> tmp = entity.getClass();
			while (tmp != null && tmp != Object.class) {
				final Field[] declaredFields = tmp.getDeclaredFields();
				for (final Field field : declaredFields) {
					if (filterField(field)) {
						fields.add(field);
					}
				}
				tmp = tmp.getSuperclass();
			}
			fieldCache.put(entity.getClass(), fields);
		}
		return fields;
	}

	private boolean filterField(final Field field) {
		return !field.isSynthetic() && !Modifier.isStatic(field.getModifiers());
	}

	private void serializeField(final Object entity, final VPackBuilder builder, final Field field)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackBuilderException {

		final String fieldName = getFieldName(field);
		final Class<?> type = field.getType();
		final Object value = getEntityValue(entity, field);
		addValue(field, fieldName, type, value, builder);
	}

	private String getFieldName(final Field field) {
		String fieldName = fieldNameCache.get(field);
		if (fieldName == null) {
			final SerializedName annotation = field.getAnnotation(SerializedName.class);
			fieldName = annotation != null ? annotation.value() : field.getName();
			fieldNameCache.put(field, fieldName);
		}
		return fieldName;
	}

	private void addValue(
		final Field field,
		final String name,
		final Class<?> type,
		final Object value,
		final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackBuilderException {

		if (value == null) {
			add(name, new Value(ValueType.NULL), builder);
		} else if (type == Boolean.class || type == boolean.class) {
			add(name, new Value(Boolean.class.cast(value)), builder);
		} else if (type == Integer.class || type == int.class) {
			add(name, new Value(Integer.class.cast(value)), builder);
		} else if (type == Long.class || type == long.class) {
			add(name, new Value(Long.class.cast(value)), builder);
		} else if (type == Float.class || type == float.class) {
			add(name, new Value(Float.class.cast(value)), builder);
		} else if (type == Short.class || type == short.class) {
			add(name, new Value(Short.class.cast(value)), builder);
		} else if (type == Double.class || type == double.class) {
			add(name, new Value(Double.class.cast(value)), builder);
		} else if (type == BigInteger.class) {
			add(name, new Value(BigInteger.class.cast(value)), builder);
		} else if (type == BigDecimal.class) {
			add(name, new Value(BigDecimal.class.cast(value)), builder);
		} else if (type == String.class) {
			add(name, new Value(String.class.cast(value)), builder);
		} else if (type == Character.class || type == char.class) {
			add(name, new Value(Character.class.cast(value)), builder);
		} else if (type.isArray()) {
			addArray(name, value, builder);
		} else if (type.isEnum()) {
			add(name, new Value(Enum.class.cast(value).name()), builder);
		} else if (Iterable.class.isAssignableFrom(type)) {
			addIterable(name, value, builder);
		} else if (Map.class.isAssignableFrom(type)) {
			addMap(field, name, type, value, builder);
		} else {
			serializeInternal(name, value, builder);
		}
	}

	private void addArray(final String name, final Object value, final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackBuilderException {
		add(name, new Value(ValueType.ARRAY), builder);
		for (int i = 0; i < Array.getLength(value); i++) {
			final Object element = Array.get(value, i);
			addValue(null, null, element.getClass(), element, builder);
		}
		builder.close();
	}

	private void addIterable(final String name, final Object value, final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackBuilderException {
		add(name, new Value(ValueType.ARRAY), builder);
		for (final Iterator iterator = Iterable.class.cast(value).iterator(); iterator.hasNext();) {
			final Object element = iterator.next();
			addValue(null, null, element.getClass(), element, builder);
		}
		builder.close();
	}

	private void addMap(
		final Field field,
		final String name,
		final Class<?> type,
		final Object value,
		final VPackBuilder builder)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, VPackBuilderException {
		final Map map = Map.class.cast(value);
		if (map.size() > 0) {
			final Class<?> keyType = getComponentKeyType(field, type);
			if (isStringableKeyType(keyType)) {
				add(name, new Value(ValueType.OBJECT), builder);
				final Set<Entry<?, ?>> entrySet = map.entrySet();
				for (final Entry<?, ?> entry : entrySet) {
					addValue(null, keyToString(entry.getKey()), entry.getValue().getClass(), entry.getValue(), builder);
				}
				builder.close();
			} else {
				add(name, new Value(ValueType.ARRAY), builder);
				final Set<Entry<?, ?>> entrySet = map.entrySet();
				for (final Entry<?, ?> entry : entrySet) {
					add(null, new Value(ValueType.OBJECT), builder);
					addValue(null, ATTR_KEY, entry.getKey().getClass(), entry.getKey(), builder);
					addValue(null, ATTR_VALUE, entry.getValue().getClass(), entry.getValue(), builder);
					builder.close();
				}
				builder.close();
			}
		} else {
			add(name, new Value(ValueType.OBJECT), builder);
			builder.close();
		}
	}

	private boolean isStringableKeyType(final Class<?> type) {
		return KEY_TYPES.contains(type) || Enum.class.isAssignableFrom(type);
	}

	private String keyToString(final Object key) {
		return Enum.class.isAssignableFrom(key.getClass()) ? Enum.class.cast(key).name() : key.toString();
	}

	private void add(final String name, final Value value, final VPackBuilder builder) throws VPackBuilderException {
		if (name != null) {
			builder.add(name, value);
		} else {
			builder.add(value);
		}
	}

	private Object getEntityValue(final Object entity, final Field field) throws IllegalAccessException {
		field.setAccessible(true);
		return field.get(entity);
	}

	private void setEntityValue(final Object entity, final Field field, final Object value)
			throws IllegalAccessException {
		field.setAccessible(true);
		field.set(entity, value);
	}

}
