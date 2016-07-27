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

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class ReplicationLoggerConfigEntity extends BaseEntity {

	private boolean autoStart;
	private boolean logRemoteChanges;
	private long maxEvents;
	private long maxEventsSize;

	public boolean isAutoStart() {
		return autoStart;
	}

	public boolean isLogRemoteChanges() {
		return logRemoteChanges;
	}

	public long getMaxEvents() {
		return maxEvents;
	}

	public long getMaxEventsSize() {
		return maxEventsSize;
	}

	public void setAutoStart(final boolean autoStart) {
		this.autoStart = autoStart;
	}

	public void setLogRemoteChanges(final boolean logRemoteChanges) {
		this.logRemoteChanges = logRemoteChanges;
	}

	public void setMaxEvents(final long maxEvents) {
		this.maxEvents = maxEvents;
	}

	public void setMaxEventsSize(final long maxEventsSize) {
		this.maxEventsSize = maxEventsSize;
	}

}
