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
package org.tlsfree.core.persistence;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
public abstract class AbstractAuditingEntity {
	public static final int STATUS_INACTIVE = 0;
	public static final int STATUS_ACTIVE = 1;

	@Id
	@GeneratedValue(generator = "flake")
	@GenericGenerator(name = "flake", strategy = "org.tlsfree.core.persistence.FlakeIdGenerator")
	private Long id;
	@CreatedDate
	@Column(name = "created_date") 
	private Date createdDate;
	@LastModifiedDate
	@Column(name = "last_modified_date")
	private Date lastModifiedDate;
	@Embedded
	@CreatedBy
	@AttributeOverrides({
		@AttributeOverride(name = "username", column = @Column(name = "created_by")),
		@AttributeOverride(name = "clientId", column = @Column(name = "created_by_client"))
	})
	private AuditInformation createdBy;	
	@Embedded
	@LastModifiedBy
	@AttributeOverrides({
		@AttributeOverride(name = "username", column = @Column(name = "last_modified_by")),
		@AttributeOverride(name = "clientId", column = @Column(name = "last_modified_by_client"))
	})
	private AuditInformation lastModifiedBy;
	
	private int status = STATUS_ACTIVE;

	public Long getId() {
		return id;
	}

	public AbstractAuditingEntity setId(Long id) {
		this.id = id;
		return this;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public AbstractAuditingEntity setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public AbstractAuditingEntity setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
		return this;
	}

	public AuditInformation getCreatedBy() {
		return createdBy;
	}

	public AbstractAuditingEntity setCreatedBy(AuditInformation createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public AuditInformation getLastModifiedBy() {
		return lastModifiedBy;
	}

	public AbstractAuditingEntity setLastModifiedBy(AuditInformation lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
		return this;
	}

	public int getStatus() {
		return status;
	}

	public AbstractAuditingEntity setStatus(int status) {
		this.status = status;
		return this;
	}
}
