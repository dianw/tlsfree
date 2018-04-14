package org.enkrip.account;

import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/acme-accounts")
public class AccountRestController {
	private final UserAccountService userAccountService;

	public AccountRestController(UserAccountService userAccountService) {
		this.userAccountService = userAccountService;
	}

	@GetMapping("/{id}")
	public UserAccountRestData findUserAccountById(@PathVariable("id") String accountId) throws AcmeException {
		UserAccountEntity account = userAccountService.findUserAccountById(accountId);
		Page<UserAccountContactEntity> contacts = userAccountService.findAccountContactsByAccount(accountId, null);
		return UserAccountRestData.builder(account, contacts.getContent()).build();
	}
}
