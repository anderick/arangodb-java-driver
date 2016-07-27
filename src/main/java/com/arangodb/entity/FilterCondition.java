/*
 * Copyright (C) 2012,2013 tamtam180
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

import java.io.Serializable;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class FilterCondition implements Serializable {

	private String key;
	private Object value;
	private String compare;

	public FilterCondition() {

	}

	public FilterCondition(final String key, final Object value, final String compare) {
		this.key = key;
		this.value = value;
		this.compare = compare;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public String getCompare() {
		return compare;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public void setValue(final Object value) {
		this.value = value;
	}

	public void setCompare(final String compare) {
		this.compare = compare;
	}

}
