package com.arangodb.velocypack;

import java.text.DateFormat;
import java.util.Locale;

/**
 * @author Mark - mark@arangodb.com
 *
 */
public class VPackParser {

	private static final String OBJECT_OPEN = "{";
	private static final String OBJECT_CLOSE = "}";
	private static final String ARRAY_OPEN = "[";
	private static final String ARRAY_CLOSE = "]";
	private static final String STRING = "\"";
	private static final String FIELD = ":";
	private static final String SEPERATOR = ",";
	private static final DateFormat DATEFORMAT = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT,
		Locale.US);

	public static String toJson(final VPackSlice vpack) {
		final StringBuilder json = new StringBuilder();
		parse(null, vpack, json);
		return json.toString();
	}

	private static void parse(final VPackSlice attribute, final VPackSlice value, final StringBuilder json) {
		if (attribute != null && attribute.isString()) {
			appendField(attribute, json);
		}
		if (value.isObject()) {
			parseObject(value, json);
		} else if (value.isArray()) {
			parseArray(value, json);
		} else if (value.isBoolean()) {
			json.append(value.getAsBoolean());
		} else if (value.isString()) {
			json.append(STRING);
			json.append(value.getAsString());
			json.append(STRING);
		} else if (value.isNumber()) {
			json.append(value.getAsNumber());
		} else if (value.isDate()) {
			json.append(STRING);
			json.append(DATEFORMAT.format(value.getAsDate()));
			json.append(STRING);
		}
	}

	private static void appendField(final VPackSlice attribute, final StringBuilder json) {
		json.append(STRING);
		json.append(attribute.getAsString());
		json.append(STRING);
		json.append(FIELD);
	}

	private static void parseObject(final VPackSlice value, final StringBuilder json) {
		json.append(OBJECT_OPEN);
		for (int i = 0; i < value.getLength(); i++) {
			if (i > 0) {
				json.append(SEPERATOR);
			}
			parse(value.keyAt(i), value.valueAt(i), json);
		}
		json.append(OBJECT_CLOSE);
	}

	private static void parseArray(final VPackSlice value, final StringBuilder json) {
		json.append(ARRAY_OPEN);
		for (int i = 0; i < value.getLength(); i++) {
			if (i > 0) {
				json.append(SEPERATOR);
			}
			parse(null, value.at(i), json);
		}
		json.append(ARRAY_CLOSE);
	}

}
