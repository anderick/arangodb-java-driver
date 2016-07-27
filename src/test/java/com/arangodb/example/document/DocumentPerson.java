/*
 * Copyright (C) 2015 ArangoDB GmbH
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

package com.arangodb.example.document;

import com.arangodb.entity.BaseDocument;
import com.arangodb.velocypack.annotations.SerializedName;

/**
 * A person class.
 * 
 * The document person class has attributes to store the document key, id and
 * revision.
 * 
 * @author a-brandt
 *
 */
public class DocumentPerson {

	@SerializedName(BaseDocument.ID)
	private String documentHandle;

	@SerializedName(BaseDocument.KEY)
	private String documentKey;

	@SerializedName(BaseDocument.REV)
	private String documentRevision;

	private String name;

	private String gender;

	private Integer age;

	public DocumentPerson() {

	}

	public DocumentPerson(final String name, final String gender, final Integer age) {
		this.name = name;
		this.gender = gender;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(final Integer age) {
		this.age = age;
	}

	public String getDocumentHandle() {
		return documentHandle;
	}

	public void setDocumentHandle(final String documentHandle) {
		this.documentHandle = documentHandle;
	}

	public String getDocumentKey() {
		return documentKey;
	}

	public void setDocumentKey(final String documentKey) {
		this.documentKey = documentKey;
	}

	public String getDocumentRevision() {
		return documentRevision;
	}

	public void setDocumentRevision(final String documentRevision) {
		this.documentRevision = documentRevision;
	}

}
