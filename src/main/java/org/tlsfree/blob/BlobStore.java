/*
 * Copyright 2016 Dian Aditya.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tlsfree.blob;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.codec.digest.DigestUtils;
import org.tlsfree.core.persistence.AbstractAuditingEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "blob_store")
public class BlobStore extends AbstractAuditingEntity {
	@Lob
	@JsonIgnore
	@Column(name = "data_blob")
	@Basic(fetch = FetchType.LAZY)
	private byte[] dataBlob;
	@Column(name = "content_type")
	private String contentType;
	private long size;
	private String hash;

	public byte[] getDataBlob() {
		return dataBlob;
	}

	public BlobStore setDataBlob(byte[] dataBlob) {
		this.dataBlob = dataBlob;
		this.hash = DigestUtils.sha256Hex(dataBlob);
		this.size = dataBlob.length;
		return this;
	}

	public String getContentType() {
		return contentType;
	}

	public BlobStore setContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	public long getSize() {
		return size;
	}

	public BlobStore setSize(long size) {
		this.size = size;
		return this;
	}

	public String getHash() {
		return hash;
	}
}
