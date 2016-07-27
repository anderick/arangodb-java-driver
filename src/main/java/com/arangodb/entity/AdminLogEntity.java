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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * A entity defining admin logs
 *
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class AdminLogEntity extends BaseEntity {

	/**
	 * The total amount of log entries
	 */
	private int totalAmount;

	/**
	 * A list of log entries
	 * 
	 * @see com.arangodb.entity.AdminLogEntity.LogEntry
	 */
	private List<LogEntry> logs;

	public int getTotalAmount() {
		return totalAmount;
	}

	public List<LogEntry> getLogs() {
		return logs;
	}

	public void setTotalAmount(final int totalAmount) {
		this.totalAmount = totalAmount;
	}

	public void setLogs(final List<LogEntry> logs) {
		this.logs = logs;
	}

	public static class LogEntry implements Serializable {

		/**
		 * The log id
		 */
		private int lid;

		/**
		 * The integer representation of a log level
		 */
		private int level;

		/**
		 * The timestamp of the log entry
		 */
		private Date timestamp;

		/**
		 * The log message
		 */
		private String text;

		public int getLid() {
			return lid;
		}

		public int getLevel() {
			return level;
		}

		public Date getTimestamp() {
			return timestamp;
		}

		public String getText() {
			return text;
		}

		public void setLid(final int lid) {
			this.lid = lid;
		}

		public void setLevel(final int level) {
			this.level = level;
		}

		public void setTimestamp(final Date timestamp) {
			this.timestamp = timestamp;
		}

		public void setText(final String text) {
			this.text = text;
		}
	}

}
