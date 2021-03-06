package org.enkrip.account;

import java.util.List;
import java.util.stream.Collectors;

import org.codenergic.theskeleton.core.security.User;
import org.codenergic.theskeleton.user.UserEntity;
import org.shredzone.acme4j.exception.AcmeException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{username}/acme-accounts")
public class UserAccountRestController {
	private final UserAccountMapper userAccountMapper;
	private final UserAccountService userAccountService;

	public UserAccountRestController(UserAccountService userAccountService) {
		this.userAccountMapper = UserAccountMapper.MAPPER;
		this.userAccountService = userAccountService;
	}

	@DeleteMapping
	public void deactivateAccount(@User.Inject UserEntity user, @RequestBody String accountId) throws AcmeException {
		userAccountService.deactivateAccount(user.getId(), accountId);
	}

	private UserAccountRestData findUserAccountRestData(UserAccountEntity account) {
		Page<UserAccountContactEntity> contacts = userAccountService.findAccountContactsByAccount(account.getId(), null);
		return userAccountMapper.toUserAccountData(account, contacts.getContent());
	}

	@GetMapping
	public Page<UserAccountRestData> findUserAccountsByUser(@User.Inject UserEntity user, Pageable pageable) {
		return userAccountService.findUserAccountsByUser(user.getId(), pageable)
			.map(this::findUserAccountRestData);
	}

	@PostMapping
	public UserAccountRestData registerNewUserAccount(@User.Inject UserEntity user, @RequestBody List<String> emails) throws AcmeException {
		UserAccountEntity account = userAccountService.registerNewUserAccount(user, emails.stream()
			.map(email -> new UserAccountContactEntity().setEmail(email))
			.collect(Collectors.toList()));
		return findUserAccountRestData(account);
	}

	@PutMapping
	public UserAccountRestData updateUserAccountContacts(@User.Inject UserEntity user, @RequestBody UserAccountRestData account) throws AcmeException {
		UserAccountEntity savedAccount = userAccountService.updateUserAccountContacts(user.getId(), account.getAccountId(), account.getContacts()
			.stream().map(contact -> new UserAccountContactEntity().setEmail(contact)).collect(Collectors.toList()));
		return findUserAccountRestData(savedAccount);
	}

	@PostMapping("/key-roll-over")
	public UserAccountRestData updateUserAccountKeyPair(@User.Inject UserEntity user, @RequestBody String accountId) throws AcmeException {
		UserAccountEntity account = userAccountService.updateUserAccountKeyPair(user.getId(), accountId);
		return findUserAccountRestData(account);
	}
}
