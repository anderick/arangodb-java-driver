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
import java.util.Map;

/**
 * @author tamtam180 - kirscheless at gmail.com
 * @author gschwab
 *
 */
public class StatisticsEntity extends BaseEntity {

	private System system;
	private Client client;
	private Server server;

	public System getSystem() {
		return system;
	}

	public Client getClient() {
		return client;
	}

	public Server getServer() {
		return server;
	}

	public void setSystem(final System system) {
		this.system = system;
	}

	public void setClient(final Client client) {
		this.client = client;
	}

	public void setServer(final Server server) {
		this.server = server;
	}

	public static class FigureValue implements Serializable {
		private double sum;
		private long count;
		private long[] counts;

		public double getSum() {
			return sum;
		}

		public long getCount() {
			return count;
		}

		public long[] getCounts() {
			return counts;
		}

		public void setSum(final double sum) {
			this.sum = sum;
		}

		public void setCount(final long count) {
			this.count = count;
		}

		public void setCounts(final long[] counts) {
			this.counts = counts;
		}

	}

	public static class Client implements Serializable {
		private int httpConnections;
		private Map<String, FigureValue> figures;

		public int getHttpConnections() {
			return httpConnections;
		}

		public Map<String, FigureValue> getFigures() {
			return figures;
		}

		public void setHttpConnections(final int httpConnections) {
			this.httpConnections = httpConnections;
		}

		public void setFigures(final Map<String, FigureValue> figures) {
			this.figures = figures;
		}
	}

	public static class Server implements Serializable {
		double uptime;

		public double getUptime() {
			return uptime;
		}

		public void setUptime(final double uptime) {
			this.uptime = uptime;
		}

	}

	public static class System implements Serializable {

		private long minorPageFaults;
		private long majorPageFaults;
		private double userTime;
		private double systemTime;
		private int numberOfThreads;
		private long residentSize;
		private long virtualSize;

		public long getMinorPageFaults() {
			return minorPageFaults;
		}

		public long getMajorPageFaults() {
			return majorPageFaults;
		}

		public double getUserTime() {
			return userTime;
		}

		public double getSystemTime() {
			return systemTime;
		}

		public int getNumberOfThreads() {
			return numberOfThreads;
		}

		public long getResidentSize() {
			return residentSize;
		}

		public long getVirtualSize() {
			return virtualSize;
		}

		public void setMinorPageFaults(final long minorPageFaults) {
			this.minorPageFaults = minorPageFaults;
		}

		public void setMajorPageFaults(final long majorPageFaults) {
			this.majorPageFaults = majorPageFaults;
		}

		public void setUserTime(final double userTime) {
			this.userTime = userTime;
		}

		public void setSystemTime(final double systemTime) {
			this.systemTime = systemTime;
		}

		public void setNumberOfThreads(final int numberOfThreads) {
			this.numberOfThreads = numberOfThreads;
		}

		public void setResidentSize(final long residentSize) {
			this.residentSize = residentSize;
		}

		public void setVirtualSize(final long virtualSize) {
			this.virtualSize = virtualSize;
		}

	}

}
