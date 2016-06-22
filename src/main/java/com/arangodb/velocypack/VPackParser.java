package com.arangodb.velocypack;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.arangodb.velocypack.exception.VPackBuilderException;
import com.arangodb.velocypack.exception.VPackBuilderKeyAlreadyWrittenException;
import com.arangodb.velocypack.exception.VPackBuilderNeedOpenCompoundException;
import com.arangodb.velocypack.exception.VPackBuilderNeedOpenObjectException;
import com.arangodb.velocypack.exception.VPackBuilderNumberOutOfRangeException;
import com.arangodb.velocypack.exception.VPackBuilderUnexpectedValueException;
import com.arangodb.velocypack.exception.VPackParserException;
import com.arangodb.velocypack.exception.VPackValueTypeException;
import com.arangodb.velocypack.util.Value;
import com.arangodb.velocypack.util.ValueType;

/**
 * @author Mark - mark@arangodb.com
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class VPackParser {

	private static final String SET = "set";
	private static final String GET = "get";
	private static final String IS = "is";

	private final Map<Class<?>, VPackTypeAdapter<?>> adapters;
	private final Map<Class<?>, VPackInstanceCreator<?>> instanceCreators;

	public VPackParser() {
		super();
		adapters = new HashMap<Class<?>, VPackTypeAdapter<?>>();
		instanceCreators = new HashMap<Class<?>, VPackInstanceCreator<?>>();
		VPackDefautInstanceCreators.registerInstanceCreators(this);
	}

	public <T> void registerTypeAdatper(final Class<T> clazz, final VPackTypeAdapter<T> adapter) {
		adapters.put(clazz, adapter);
	}

	public <T> void regitserInstanceCreator(final Class<T> clazz, final VPackInstanceCreator<T> creator) {
		instanceCreators.put(clazz, creator);
	}

	public <T> T toEntity(final VPackSlice vpack, final Class<T> type) throws VPackParserException {
		final T entity;
		try {
			entity = toEntityInternal(vpack, type);
		} catch (final Exception e) {
			throw new VPackParserException(e);
		}
		return entity;
	}

	private <T> T toEntityInternal(final VPackSlice vpack, final Class<T> type)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		final T entity;
		final VPackTypeAdapter<T> adapter = (VPackTypeAdapter<T>) adapters.get(type);
		if (adapter != null) {
			entity = adapter.toEntity(vpack);
		} else {
			entity = createInstance(type);
			final Field[] declaredFields = getDeclaredFields(entity);
			for (final Field field : declaredFields) {
				toField(vpack, entity, field);
			}
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

	private void toField(final VPackSlice vpack, final Object entity, final Field field)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException {
		final VPackSlice attr = vpack.get(field.getName());
		if (!attr.isNone()) {
			final Object value = getValue(attr, field, field.getType());
			setValue(entity, field.getName(), field.getType(), value);
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
			result = "java.lang.Enum<?>".equals(argType.toString()) ? Enum.class : (Class<?>) argType;
		}
		return result;
	}

	private <T> Object getValue(final VPackSlice vpack, final Field field, final Class<T> type)
			throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		final Object value;
		if (type == Boolean.class || type == boolean.class) {
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
			final Class<?> componentType = getComponentType(field, type);
			final Object tmpValue = Array.newInstance(componentType, (int) vpack.getLength());
			for (int i = 0; i < vpack.getLength(); i++) {
				Array.set(tmpValue, i, getValue(vpack.at(i), null, componentType));
			}
			value = tmpValue;
		} else if (type.isEnum()) {
			final Class<? extends Enum> enumType = (Class<? extends Enum>) type;
			value = Enum.valueOf(enumType, vpack.getAsString());
		} else if (Collection.class.isAssignableFrom(type)) {
			final Class<?> componentType = getComponentType(field, type);
			final Collection tmpValue = (Collection) createInstance(type);
			for (int i = 0; i < vpack.getLength(); i++) {
				tmpValue.add(getValue(vpack.at(i), null, componentType));
			}
			value = tmpValue;
		} else if (Map.class.isAssignableFrom(type)) {
			final Class<?> keyType = getComponentKeyType(field, type);
			final Class<?> valueType = getComponentValueType(field, type);
			if (isStringableKeyType(keyType)) {
				final Map map = new HashMap();
				for (int i = 0; i < vpack.getLength(); i++) {
					map.put(fromStringKey(vpack.keyAt(i).getAsString(), keyType),
						getValue(vpack.valueAt(i), null, valueType));
				}
				value = map;
			} else {
				// TODO
				value = null;
			}
		} else {
			value = toEntityInternal(vpack, type);
		}
		return value;
	}

	private Object fromStringKey(final String key, final Class<?> type) {
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
		} else if (type == Integer.class) {
			final Class<? extends Enum> enumType = (Class<? extends Enum>) type;
			result = Enum.valueOf(enumType, key);
		} else {
			result = null;// TODO
		}
		return result;
	}

	public VPackSlice fromEntity(final Object entity) throws VPackParserException {
		final VPackBuilder builder = new VPackBuilder();
		try {
			fromEntityInternal(null, entity, builder);
		} catch (final Exception e) {
			throw new VPackParserException(e);
		}
		return builder.slice();
	}

	private void fromEntityInternal(final String name, final Object entity, final VPackBuilder builder)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, VPackValueTypeException, VPackBuilderException {

		add(name, new Value(ValueType.Object), builder);
		final VPackTypeAdapter<Object> adapter = (VPackTypeAdapter<Object>) adapters.get(entity.getClass());
		if (adapter != null) {
			adapter.fromEntity(builder, entity.getClass());
		} else {
			final Field[] fields = getDeclaredFields(entity);
			for (final Field field : fields) {
				fromField(entity, builder, field);
			}
		}
		builder.close();
	}

	private Field[] getDeclaredFields(final Object entity) {
		final Collection<Field> fields = new ArrayList<Field>();
		Class<?> tmp = entity.getClass();
		while (tmp != null && tmp != Object.class) {
			fields.addAll(Arrays.asList(tmp.getDeclaredFields()));
			tmp = tmp.getSuperclass();
		}
		return fields.toArray(new Field[] {});
	}

	private void fromField(final Object entity, final VPackBuilder builder, final Field field)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
			VPackBuilderNeedOpenObjectException, VPackBuilderKeyAlreadyWrittenException,
			VPackBuilderUnexpectedValueException, VPackBuilderNumberOutOfRangeException, VPackBuilderException {

		final String fieldName = field.getName();
		final Class<?> type = field.getType();
		final Object value = getEntityValue(entity, fieldName, type);
		addValue(field, fieldName, type, value, builder);
	}

	private void addValue(
		final Field field,
		final String name,
		final Class<?> type,
		final Object value,
		final VPackBuilder builder) throws VPackBuilderNeedOpenObjectException, VPackBuilderKeyAlreadyWrittenException,
			VPackBuilderUnexpectedValueException, VPackBuilderNumberOutOfRangeException,
			VPackBuilderNeedOpenCompoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, VPackBuilderException {

		if (value == null) {
			// TODO
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
			add(name, new Value(ValueType.Array), builder);
			for (int i = 0; i < Array.getLength(value); i++) {
				final Object element = Array.get(value, i);
				addValue(null, null, element.getClass(), element, builder);
			}
			builder.close();
		} else if (type.isEnum()) {
			add(name, new Value(Enum.class.cast(value).name()), builder);
		} else if (Iterable.class.isAssignableFrom(type)) {
			add(name, new Value(ValueType.Array), builder);
			for (final Iterator iterator = Iterable.class.cast(value).iterator(); iterator.hasNext();) {
				final Object element = iterator.next();
				addValue(null, null, element.getClass(), element, builder);
			}
			builder.close();
		} else if (Map.class.isAssignableFrom(type)) {
			final Class<?> keyType = getComponentKeyType(field, type);
			if (isStringableKeyType(keyType)) {
				add(name, new Value(ValueType.Object), builder);
				final Set<Entry<?, ?>> entrySet = Map.class.cast(value).entrySet();
				for (final Entry<?, ?> entry : entrySet) {
					addValue(null, keyToString(entry.getKey()), entry.getValue().getClass(), entry.getValue(), builder);
				}
				builder.close();
			} else {
				// TODO
			}
		} else {
			fromEntityInternal(name, value, builder);
		}
	}

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

	private boolean isStringableKeyType(final Class<?> type) {
		return KEY_TYPES.contains(type) || Enum.class.isAssignableFrom(type);
	}

	private String keyToString(final Object key) {
		return Enum.class.isAssignableFrom(key.getClass()) ? Enum.class.cast(key).name() : key.toString();
	}

	private void add(final String name, final Value value, final VPackBuilder builder)
			throws VPackBuilderNeedOpenObjectException, VPackBuilderKeyAlreadyWrittenException,
			VPackBuilderUnexpectedValueException, VPackBuilderNumberOutOfRangeException {
		if (name != null) {
			builder.add(name, value);
		} else {
			builder.add(value);
		}
	}

	private Object getEntityValue(final Object entity, final String fieldName, final Class<?> type)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final StringBuilder methodName = new StringBuilder();
		methodName.append(type == boolean.class ? IS : GET);
		methodName.append(fieldName.substring(0, 1).toUpperCase());
		if (fieldName.length() > 1) {
			methodName.append(fieldName.substring(1, fieldName.length()));
		}
		final Method getter = entity.getClass().getMethod(methodName.toString());
		final Object value = getter.invoke(entity);
		return value;
	}

	private <T> void setValue(final Object entity, final String fieldName, final Class<T> type, final Object value)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		final StringBuilder methodName = new StringBuilder();
		methodName.append(SET);
		methodName.append(fieldName.substring(0, 1).toUpperCase());
		if (fieldName.length() > 1) {
			methodName.append(fieldName.substring(1, fieldName.length()));
		}
		final Method setter = entity.getClass().getMethod(methodName.toString(), type);
		setter.invoke(entity, value);
	}

}
