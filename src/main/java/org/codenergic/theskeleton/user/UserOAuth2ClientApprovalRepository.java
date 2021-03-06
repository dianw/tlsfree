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
package org.codenergic.theskeleton.user;

import org.codenergic.theskeleton.core.data.AuditingEntityRepository;

import java.util.List;

public interface UserOAuth2ClientApprovalRepository extends AuditingEntityRepository<UserOAuth2ClientApprovalEntity> {
	void deleteByUserUsernameAndClientId(String username, String clientId);

	List<UserOAuth2ClientApprovalEntity> findByUserUsername(String username);

	List<UserOAuth2ClientApprovalEntity> findByUserUsernameAndClientId(String userId, String clientId);

	UserOAuth2ClientApprovalEntity findByUserUsernameAndClientIdAndScope(String userId, String clientId, String scope);
}
