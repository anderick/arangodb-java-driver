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

package com.arangodb.util;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import com.arangodb.velocypack.VPackSlice;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class DateUtils {

	private static TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

	private static ThreadLocal<LocalFormat> dateFormats = new ThreadLocal<LocalFormat>() {
		@Override
		protected LocalFormat initialValue() {
			return new LocalFormat();
		}
	};

	private DateUtils() {
		// this is a helper class
	}

	static class TripleKey<T extends Serializable, U extends Serializable, V extends Serializable>
			implements Serializable {
		private static final long serialVersionUID = 2612228100559578823L;
		private T first;
		private U second;
		private V third;

		public TripleKey() {
			// do nothing here
		}

		public TripleKey(final T first, final U second, final V third) {
			this.first = first;
			this.second = second;
			this.third = third;
		}

		public T getFirst() {
			return first;
		}

		public void setFirst(final T first) {
			this.first = first;
		}

		public U getSecond() {
			return second;
		}

		public void setSecond(final U second) {
			this.second = second;
		}

		public V getThird() {
			return third;
		}

		public void setThird(final V third) {
			this.third = third;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((first == null) ? 0 : first.hashCode());
			result = prime * result + ((second == null) ? 0 : second.hashCode());
			result = prime * result + ((third == null) ? 0 : third.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TripleKey<?, ?, ?> other = (TripleKey<?, ?, ?>) obj;
			if (first == null) {
				if (other.first != null) {
					return false;
				}
			} else if (!first.equals(other.first)) {
				return false;
			}
			if (second == null) {
				if (other.second != null) {
					return false;
				}
			} else if (!second.equals(other.second)) {
				return false;
			}
			if (third == null) {
				if (other.third != null) {
					return false;
				}
			} else if (!third.equals(other.third)) {
				return false;
			}
			return true;
		}
	}

	private static class LocalFormat {
		private final HashMap<TripleKey<String, Locale, TimeZone>, SimpleDateFormat> sdfMap = new HashMap<DateUtils.TripleKey<String, Locale, TimeZone>, SimpleDateFormat>();

		public SimpleDateFormat getDateFormat(final String format, final Locale locale, final TimeZone timeZone) {
			final TripleKey<String, Locale, TimeZone> key = new TripleKey<String, Locale, TimeZone>(format, locale,
					timeZone);
			SimpleDateFormat sdf = sdfMap.get(key);
			if (sdf == null) {
				sdf = new SimpleDateFormat(format);
				sdf.setTimeZone(timeZone);
				sdfMap.put(key, sdf);
			}
			return sdf;
		}

	}

	public static Date parse(final String text, final String format) throws ParseException {
		final SimpleDateFormat dateFormat = dateFormats.get().getDateFormat(format, Locale.US, utcTimeZone);
		return dateFormat.parse(text);
	}

	/**
	 * Parse vpack in date vapck can be date or string in format
	 * "yyyy-MM-dd'T'HH:mm:ss'Z'"
	 * 
	 * @param vpack
	 * @return a Date object
	 */
	public static Date parse(final VPackSlice vpack) {
		Date date = null;
		if (vpack.isDate()) {
			date = vpack.getAsDate();
		} else if (vpack.isString()) {
			try {
				date = DateUtils.parse(vpack.getAsString(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
			} catch (final ParseException e) {
				new RuntimeException(e);
			}
		}
		return date;
	}

	public static String format(final Date date, final String format) {
		final SimpleDateFormat dateFormat = dateFormats.get().getDateFormat(format, Locale.US, TimeZone.getDefault());
		return dateFormat.format(date);
	}

}
