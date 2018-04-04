package org.codenergic.theskeleton.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.role.RoleEntity;
import org.codenergic.theskeleton.role.RoleRestData;
import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = { UserRestController.class }, secure = false)
@EnableRestDocs
public class UserRestControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private UserAdminService userAdminService;
	@MockBean
	private UserService userService;
	@MockBean
	private TokenStoreService tokenStoreService;
	@Test
	public void testSerializeDeserializeUser() throws IOException {
		UserRestData user = UserRestData.builder()
				.id("123")
				.username("user")
				.build();
		String json = objectMapper.writeValueAsString(user);
		UserRestData user2 = objectMapper.readValue(json, UserRestData.class);
		assertThat(user).isEqualTo(user2);
	}

	@Test
	public void testAddRoleToUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("123")
				.setUsername("user123")
				.setPassword("123456");
		when(userAdminService.addRoleToUser("user123", "role123")).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/roles")
					.content("{\"role\": \"role123\"}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-role-create"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userAdminService).addRoleToUser("user123", "role123");
	}

	@Test
	public void testDeleteUser() throws Exception {
		mockMvc.perform(delete("/api/users/user123")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-delete"));
	}

	@Test
	public void testEnableOrDisableUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setEnabled(true);
		when(userAdminService.enableOrDisableUser("user123", true)).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/enable")
					.content("{\"enabled\": true}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-enable"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userAdminService).enableOrDisableUser("user123", true);
	}

	@Test
	public void testExtendsUserExpiration() throws Exception {
		final UserEntity user = new UserEntity()
				.setExpiredAt(new Date());
		when(userAdminService.extendsUserExpiration("user123", 60)).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/exp")
					.content("{\"amount\": 60}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-extend-exp"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userAdminService).extendsUserExpiration("user123", 60);
	}

	@Test
	public void testFindRolesByUserUsername() throws Exception {
		final RoleEntity role = new RoleEntity()
				.setId("role123")
				.setCode("role123");
		final Set<RoleEntity> roles = new HashSet<>(Arrays.asList(role));
		final Set<RoleRestData> expected = roles.stream()
				.map(r -> RoleRestData.builder(r).build())
				.collect(Collectors.toSet());
		when(userAdminService.findRolesByUserUsername("user123")).thenReturn(roles);
		ResultActions resultActions = mockMvc.perform(get("/api/users/user123/roles")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-role-read"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray()).isEqualTo(objectMapper.writeValueAsBytes(expected));
		verify(userAdminService).findRolesByUserUsername("user123");
	}

	@Test
	public void testFindUserByEmail() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		when(userService.findUserByEmail("user@server")).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(get("/api/users/user@server?email=true")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-read-email"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userService).findUserByEmail("user@server");
	}

	@Test
	public void testFindUserByUsername() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		when(userService.findUserByUsername("user123")).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(get("/api/users/user123")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-read"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userService).findUserByUsername("user123");
	}

	@Test
	public void testFindUsersByUsernameStartingWith() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123")
				.setEmail("user@server");
		final Page<UserEntity> users = new PageImpl<>(Arrays.asList(user));
		when(userAdminService.findUsersByUsernameStartingWith(eq("user123"), any())).thenReturn(users);
		ResultActions resultActions = mockMvc.perform(get("/api/users?username=user123")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-read-username-startingwith"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(
					users.map(u -> UserRestData.builder(u).build())));
		verify(userAdminService).findUsersByUsernameStartingWith(eq("user123"), any());
	}

	@Test
	public void testLockOrUnlockUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setAccountNonLocked(true);
		when(userAdminService.lockOrUnlockUser("user123", true)).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/lock")
					.content("{\"enabled\": true}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-lock"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userAdminService).lockOrUnlockUser("user123", true);
	}

	@Test
	public void testRemoveRoleFromUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userAdminService.removeRoleFromUser("user123", "role123")).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(delete("/api/users/user123/roles")
					.content("{\"role\": \"role123\"}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-role-delete"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userAdminService).removeRoleFromUser("user123", "role123");
	}

	@Test
	public void testSaveUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userAdminService.saveUser(any())).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(post("/api/users")
					.content("{\"username\": \"user123\", \"email\": \"user@server.com\"}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-create"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userAdminService).saveUser(any());
	}

	@Test
	public void testSaveInvalidUserEmail() throws Exception {
		MockHttpServletResponse response = mockMvc.perform(post("/api/users")
			.content("{\"username\": \"user123\", \"email\": \"user123\"}")
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andReturn()
			.getResponse();
		assertThat(response.getStatus()).isEqualTo(400);
	}

	@Test
	public void testUpdateUser() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userAdminService.updateUser(eq("user123"), any())).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123")
					.content("{\"username\": \"user123\", \"email\": \"user@server.com\"}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-update"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userAdminService).updateUser(eq("user123"), any());
	}

	@Test
	public void testUpdateUserPassword() throws Exception {
		final UserEntity user = new UserEntity()
				.setId("user123");
		when(userAdminService.updateUserPassword(eq("user123"), any())).thenReturn(user);
		ResultActions resultActions = mockMvc.perform(put("/api/users/user123/password")
					.content("{\"username\": \"user123\"}")
					.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(document("user-update-password"));
		MockHttpServletResponse response = resultActions
				.andReturn()
				.getResponse();
		assertThat(response.getContentAsByteArray())
				.isEqualTo(objectMapper.writeValueAsBytes(UserRestData.builder(user).build()));
		verify(userAdminService).updateUserPassword(eq("user123"), any());
	}

}
