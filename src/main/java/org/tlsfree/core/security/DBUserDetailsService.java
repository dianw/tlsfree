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
package org.tlsfree.core.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.tlsfree.account.Account;
import org.tlsfree.account.AccountRepository;

public class DBUserDetailsService implements UserDetailsService {
	private AccountRepository accountRepository;

	public DBUserDetailsService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByUsername(username);
		if (account == null) {
			account = accountRepository.findByEmail(username);
		}

		if (account == null) throw new UsernameNotFoundException("Username or email not found");

		List<GrantedAuthority> authorities = account.isAdministrator()
				? Arrays.asList(new SimpleGrantedAuthority("ADMINISTRATOR")) : Collections.emptyList();
		
		return new AccountUserDetails(account, authorities);
	}
}
