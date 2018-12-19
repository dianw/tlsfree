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
package org.codenergic.theskeleton.post;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PostService {
	@PreAuthorize("isAuthenticated()")
	void deletePost(@NotNull String id);

	@PreAuthorize("permitAll()")
	Page<PostEntity> findPostByContentContaining(@NotNull String content, Pageable pageable);

	@PreAuthorize("isAuthenticated() and principal.id == #followerId")
	Page<PostEntity> findPostByFollowerId(String followerId, Pageable pageable);

	@PreAuthorize("permitAll()")
	Optional<PostEntity> findPostById(@NotNull String id);

	@PreAuthorize("isAuthenticated() and principal.id == #userId")
	Page<PostEntity> findPostByPosterId(String userId, Pageable pageable);

	@PreAuthorize("permitAll()")
	Page<PostEntity> findPostReplies(String postId, Pageable pageable);

	@PreAuthorize("isAuthenticated()")
	PostEntity replyPost(@NotNull String postId, @NotNull @Valid PostEntity replyPost);

	@PreAuthorize("isAuthenticated() and principal == #currentUser")
	PostEntity savePost(UserEntity currentUser, @NotNull @Valid PostEntity post);

	@PreAuthorize("isAuthenticated()")
	PostEntity updatePost(@NotNull String id, @NotNull @Valid PostEntity post);
}
