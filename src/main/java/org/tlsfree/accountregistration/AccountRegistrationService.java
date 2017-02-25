package org.tlsfree.accountregistration;

import java.io.IOException;

import javax.transaction.Transactional;

import org.bouncycastle.cms.CMSException;
import org.shredzone.acme4j.exception.AcmeException;
import org.tlsfree.registration.Registration;

@Transactional
public interface AccountRegistrationService {
	Registration createRegistration(Long accountId) throws AcmeException, CMSException, IOException;

	Registration getRegistrationById(Long registrationId) throws CMSException, IOException;
}
