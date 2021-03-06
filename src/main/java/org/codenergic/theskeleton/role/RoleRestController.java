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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
public class RoleRestController {
	private final RoleService roleService;
	private final RoleMapper roleMapper = RoleMapper.newInstance();

	public RoleRestController(RoleService roleService) {
		this.roleService = roleService;
	}

	@DeleteMapping("/{idOrCode}")
	public void deleteRole(@PathVariable("idOrCode") final String idOrCode) {
		roleService.deleteRole(idOrCode);
	}

	@GetMapping("/{idOrCode}")
	public ResponseEntity<RoleRestData> findRoleByIdOrCode(@PathVariable("idOrCode") final String idOrCode) {
		return roleService.findRoleByIdOrCode(idOrCode)
			.map(roleMapper::toRoleData)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public Page<RoleRestData> findRoles(@RequestParam(name = "q", defaultValue = "") final String keywords,
			final Pageable pageable) {
		return roleService.findRoles(keywords, pageable)
				.map(roleMapper::toRoleData);
	}

	@PostMapping
	public RoleRestData saveRole(@RequestBody @Validated(RoleRestData.New.class) final RoleRestData roleData) {
		final RoleEntity role = roleMapper.toRole(roleData);
		return roleMapper.toRoleData(roleService.saveRole(role));
	}

	@PutMapping("/{code}")
	public RoleRestData updateRole(@PathVariable("code") String code, @RequestBody @Validated(RoleRestData.Existing.class) final RoleRestData roleData) {
		final RoleEntity role = roleMapper.toRole(roleData);
		return roleMapper.toRoleData(roleService.updateRole(code, role));
	}
}
