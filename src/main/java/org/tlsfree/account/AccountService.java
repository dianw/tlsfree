/*
 * Copyright 2016 the original author or authors.
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
package org.tlsfree.account;

import org.springframework.transaction.annotation.Transactional;
import org.tlsfree.core.persistence.ReadOnlyTransactional;

@Transactional
public interface AccountService {
	@ReadOnlyTransactional
	Account getAccountById(Long id);

	boolean isAccountPasswordMatch(String rawPassword, Account account);

	Account saveAccount(Account account);

	Account updateAccountPassword(Long accountId, String newPassword);
}
