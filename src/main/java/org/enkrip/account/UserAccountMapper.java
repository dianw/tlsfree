package org.enkrip.account;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.mapstruct.IterableMapping;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserAccountMapper {
	static UserAccountMapper MAPPER = Mappers.getMapper(UserAccountMapper.class);

//	public static Builder builder(UserAccountEntity userAccount, List<UserAccountContactEntity> contacts) {
//		Builder builder = builder()
//			.accountId(userAccount.getId())
//			.acmeAccountLocation(userAccount.getAccountLocation())
//			.publicKey(Base64.encodeBase64String(userAccount.getAccountPublicKey()))
//			.publicKeySha1Hash(DigestUtils.sha1Hex(userAccount.getAccountPublicKey()));
//		for (UserAccountContactEntity contact : contacts) {
//			builder.contactsBuilder().add(contact.getEmail());
//		}
//		return builder;
//	}

	default Set<String> toContactEmails(List<UserAccountContactEntity> contacts) {
		return contacts.stream().map(UserAccountContactEntity::getEmail).collect(Collectors.toSet());
	}

	default String toBase64(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	default String toSha1(byte[] bytes) {
		return DigestUtils.sha1Hex(bytes);
	}

	@Mapping(target = "accountId", source = "userAccount.id")
	@Mapping(target = "acmeAccountLocation", source = "userAccount.accountLocation")
	@Mapping(target = "publicKey", expression = "java(toBase64(userAccount.getAccountPublicKey()))")
	@Mapping(target = "publicKeySha1Hash", expression = "java(toSha1(userAccount.getAccountPublicKey()))")
	@Mapping(target = "contacts", expression = "java(toContactEmails(userAccountContact))")
	UserAccountRestData toUserAccountData(UserAccountEntity userAccount, List<UserAccountContactEntity> userAccountContact);
}
