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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class IOUtils {

	private static Logger logger = LoggerFactory.getLogger(IOUtils.class);

	private IOUtils() {
		// this is a helper class
	}

	public static byte[] toByteArray(final InputStream input) throws IOException {
		BufferedInputStream in = null;
		ByteArrayOutputStream out = null;
		byte[] result;
		try {
			in = new BufferedInputStream(input);
			out = new ByteArrayOutputStream();
			final byte[] buf = new byte[8012];
			int len;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			result = out.toByteArray();
		} finally {
			close(in);
			close(out);
		}
		return result;

	}

	public static String toString(final InputStream input) throws IOException {
		return toString(input, "utf-8");
	}

	public static String toString(final InputStream input, final String encode) throws IOException {

		InputStreamReader in;
		try {

			final StringBuilder buffer = new StringBuilder(8012);

			in = new InputStreamReader(new BufferedInputStream(input), encode);
			final char[] cbuf = new char[8012];
			int len;
			while ((len = in.read(cbuf)) != -1) {
				buffer.append(cbuf, 0, len);
			}

			return buffer.toString();

		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} finally {
			close(input);
		}
	}

	public static void close(final Closeable input) {
		if (input != null) {
			try {
				input.close();
			} catch (final IOException e) {
				logger.error("could not close a file", e);
			}
		}
	}

}
