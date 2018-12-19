/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.role;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;
import org.codenergic.theskeleton.user.UserEntity;

@SuppressWarnings("serial")
@Entity
@Table(name = "ts_user_role", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "role_id" }) })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class UserRoleEntity extends AbstractAuditingEntity {
	@NotNull
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity user;
	@NotNull
	@ManyToOne
	@JoinColumn(name = "role_id")
	private RoleEntity role;

	public UserRoleEntity() {
		// initialize UserRoleEntity
	}

	public UserRoleEntity(UserEntity user, RoleEntity role) {
		this.user = user;
		this.role = role;
	}

	@Override
	public UserRoleEntity setId(String id) {
		super.setId(id);
		return this;
	}

	public UserEntity getUser() {
		return user;
	}

	public UserRoleEntity setUser(UserEntity user) {
		this.user = user;
		return this;
	}

	public RoleEntity getRole() {
		return role;
	}

	public UserRoleEntity setRole(RoleEntity role) {
		this.role = role;
		return this;
	}
}
