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
package org.tlsfree.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.tlsfree.account.AccountRepository;
import org.tlsfree.accountregistration.AccountRegistrationService;
import org.tlsfree.core.security.AuthenticationSuccessListener;
import org.tlsfree.core.security.DBUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private AccountRepository accountRepository;

	public WebSecurityConfig(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// auth.userDetailsService(dbUserDetailsService()).passwordEncoder(passwordEncoder());
		auth.inMemoryAuthentication()
			.withUser("administrator")
			.password("admin123")
			.roles("ADMINISTRATOR");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/**").permitAll()
				.antMatchers("/oauth/token").permitAll()
				.and()
			.rememberMe()
				.rememberMeParameter("remember_me")
				.and()
			.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/auth/login")
				.permitAll()
				.and()
			.logout()
				.logoutUrl("/auth/logout")
				.and()
			.csrf()
				.disable()
			.anonymous();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public UserDetailsService dbUserDetailsService() {
		return new DBUserDetailsService(accountRepository);
	}

	@Bean
	public AuthenticationSuccessListener authSuccessListener(AccountRegistrationService registrationService) {
		return new AuthenticationSuccessListener(registrationService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
