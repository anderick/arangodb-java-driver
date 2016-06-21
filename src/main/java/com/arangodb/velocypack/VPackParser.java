package com.arangodb.velocypack;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	public VPackParser() {
		super();
		adapters = new HashMap<Class<?>, VPackTypeAdapter<?>>();
	}

	public <T> void registerTypeAdatper(final Class<T> clazz, final VPackTypeAdapter<T> adapter) {
		adapters.put(clazz, adapter);
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
		if (adapters.containsKey(type)) {
			final VPackTypeAdapter<T> adapter = (VPackTypeAdapter<T>) adapters.get(type);
			entity = adapter.toEntity(vpack);
		} else {
			entity = type.newInstance();
			final Field[] declaredFields = getDeclaredFields(entity);
			for (final Field field : declaredFields) {
				toField(vpack, entity, field);
			}
		}
		return entity;
	}

	private void toField(final VPackSlice vpack, final Object entity, final Field field)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, InstantiationException {
		final VPackSlice attr = vpack.get(field.getName());
		if (!attr.isNone()) {
			final Object value = getValue(attr, field.getType());
			setValue(entity, field.getName(), field.getType(), value);
		}
	}

	private <T> Object getValue(final VPackSlice vpack, final Class<T> type)
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
			final Object tmpValue = Array.newInstance(type.getComponentType(), (int) vpack.getLength());
			// final Collection<T> tmpValue = new ArrayList<T>();
			for (int i = 0; i < vpack.getLength(); i++) {
				Array.set(tmpValue, i, getValue(vpack.at(i), type.getComponentType()));
			}
			value = tmpValue;
		} else if (type.isEnum()) {
			final Class<? extends Enum> enumType = (Class<? extends Enum>) type;
			value = Enum.valueOf(enumType, vpack.getAsString());
		} else if (type.isInterface()) {
			value = null;
		} else {
			value = toEntityInternal(vpack, type);
		}
		return value;
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
		if (adapters.containsKey(entity.getClass())) {
			final VPackTypeAdapter<Object> adapter = (VPackTypeAdapter<Object>) adapters.get(entity.getClass());
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
		addValue(fieldName, type, value, builder);
	}

	private void addValue(final String name, final Class<?> type, final Object value, final VPackBuilder builder)
			throws VPackBuilderNeedOpenObjectException, VPackBuilderKeyAlreadyWrittenException,
			VPackBuilderUnexpectedValueException, VPackBuilderNumberOutOfRangeException,
			VPackBuilderNeedOpenCompoundException, NoSuchMethodException, IllegalAccessException,
			InvocationTargetException, VPackBuilderException {

		if (type == Boolean.class || type == boolean.class) {
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
				addValue(null, element.getClass(), element, builder);
			}
			builder.close();
		} else if (type.isEnum()) {
			add(name, new Value(Enum.class.cast(value).name()), builder);
		} else {
			fromEntityInternal(name, value, builder);
		}
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