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
import java.math.BigDecimal;
import java.util.List;

/**
 * @author tamtam180 - kirscheless at gmail.com
 *
 */
public class StatisticsDescriptionEntity extends BaseEntity {

	private List<Group> groups;
	private List<Figure> figures;

	public List<Group> getGroups() {
		return groups;
	}

	public List<Figure> getFigures() {
		return figures;
	}

	public void setGroups(final List<Group> groups) {
		this.groups = groups;
	}

	public void setFigures(final List<Figure> figures) {
		this.figures = figures;
	}

	public static class Group implements Serializable {
		private String group;
		private String name;
		private String description;

		public String getGroup() {
			return group;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public void setGroup(final String group) {
			this.group = group;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setDescription(final String description) {
			this.description = description;
		}

	}

	public static class Figure implements Serializable {
		private String group;
		private String identifier;
		private String name;
		private String description;
		private String type;
		private String units;
		private BigDecimal[] cuts;

		public String getGroup() {
			return group;
		}

		public String getIdentifier() {
			return identifier;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

		public String getType() {
			return type;
		}

		public String getUnits() {
			return units;
		}

		public BigDecimal[] getCuts() {
			return cuts;
		}

		public void setGroup(final String group) {
			this.group = group;
		}

		public void setIdentifier(final String identifier) {
			this.identifier = identifier;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setDescription(final String description) {
			this.description = description;
		}

		public void setType(final String type) {
			this.type = type;
		}

		public void setUnits(final String units) {
			this.units = units;
		}

		public void setCuts(final BigDecimal[] cuts) {
			this.cuts = cuts;
		}
	}

}
