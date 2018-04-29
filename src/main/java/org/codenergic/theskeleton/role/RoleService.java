/*
 * Copyright 2017 the original author or authors.
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
package org.codenergic.theskeleton.role;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface RoleService {
	@PreAuthorize("hasAuthority('role_delete')")
	void deleteRole(@NotNull String idOrCode);

	@PreAuthorize("hasAuthority('role_read')")
	RoleEntity findRoleByCode(@NotNull String code);

	@PreAuthorize("hasAuthority('role_read')")
	RoleEntity findRoleById(@NotNull String id);

	@PreAuthorize("hasAuthority('role_read')")
	RoleEntity findRoleByIdOrCode(@NotNull String idOrCode);

	@PreAuthorize("hasAuthority('role_read_all')")
	Page<RoleEntity> findRoles(Pageable pageable);

	@PreAuthorize("hasAuthority('role_read_all')")
	Page<RoleEntity> findRoles(String keyword, Pageable pageable);

	@PreAuthorize("hasAuthority('role_write')")
	RoleEntity saveRole(@NotNull @Valid RoleEntity role);

	@PreAuthorize("hasAuthority('role_update')")
	RoleEntity updateRole(@NotNull String id, @NotNull @Valid RoleEntity role);
}
