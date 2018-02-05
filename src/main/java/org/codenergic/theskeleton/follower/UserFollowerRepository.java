/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.follower;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowerRepository extends PagingAndSortingRepository<UserFollowerEntity, String> {
	long countByUserId(String userId);

	Page<UserFollowerEntity> findByFollowerId(String followerId, Pageable pageable);

	Page<UserFollowerEntity> findByUserId(String userId, Pageable pageable);

	UserFollowerEntity findByUserIdAndFollowerId(String userId, String followerId);
}
