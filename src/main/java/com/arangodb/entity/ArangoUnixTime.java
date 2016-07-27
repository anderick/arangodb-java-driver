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

package com.arangodb.entity;

/**
 * An entity representing the arango server time
 *
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ArangoUnixTime extends BaseEntity {

	/**
	 * the unix timestamp as double
	 */
	private double time;

	/**
	 * the unix timestamp as integer
	 */
	private int second;

	/**
	 * the microseconds part of the unix timestamp
	 */
	private int microsecond;

	public long getTimeMillis() {
		return 1000L * second + (microsecond / 1000);
	}

	public double getTime() {
		return time;
	}

	public int getSecond() {
		return second;
	}

	public int getMicrosecond() {
		return microsecond;
	}

	public void setTime(final double time) {
		this.time = time;
	}

	public void setSecond(final int second) {
		this.second = second;
	}

	public void setMicrosecond(final int microsecond) {
		this.microsecond = microsecond;
	}

}
