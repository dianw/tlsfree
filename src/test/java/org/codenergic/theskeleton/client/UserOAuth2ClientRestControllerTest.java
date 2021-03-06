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

package org.codenergic.theskeleton.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.codenergic.theskeleton.core.test.EnableRestDocs;
import org.codenergic.theskeleton.core.test.InjectUserDetailsService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.codenergic.theskeleton.client.OAuth2GrantType.AUTHORIZATION_CODE;
import static org.codenergic.theskeleton.client.OAuth2GrantType.IMPLICIT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@EnableSpringDataWebSupport
@WebMvcTest(controllers = {UserOAuth2ClientRestController.class})
@EnableRestDocs
@InjectUserDetailsService
public class UserOAuth2ClientRestControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockBean
	private OAuth2ClientService oAuth2ClientService;
	private final OAuth2ClientMapper oAuth2ClientMapper = OAuth2ClientMapper.newInstance();

	@Test
	@WithMockUser("user123")
	public void testFindClientByUser() throws Exception {
		final OAuth2ClientEntity client = new OAuth2ClientEntity()
			.setId("client123")
			.setName("client")
			.setDescription("description")
			.setClientSecret("123456secret")
			.setSecretRequired(true)
			.setAutoApprove(false)
			.setAuthorizedGrantTypes(new HashSet<>(Arrays.asList(AUTHORIZATION_CODE, IMPLICIT)));
		Page<OAuth2ClientEntity> clients = new PageImpl<>(Collections.singletonList(client));
		when(oAuth2ClientService.findClientByOwner(any(), any())).thenReturn(clients);
		MockHttpServletRequestBuilder request = get("/api/users/user123/clients")
			.contentType(MediaType.APPLICATION_JSON);
		MockHttpServletResponse response = mockMvc.perform(request)
			.andExpect(status().isOk())
			.andDo(document("user-client-read-all"))
			.andReturn()
			.getResponse();
		assertThat(response.getContentAsString())
			.isEqualTo(objectMapper.writeValueAsString(clients.map(oAuth2ClientMapper::toOAuth2ClientData)));
		verify(oAuth2ClientService).findClientByOwner(any(), any());
	}
}
