///*
// * Copyright 2016 Dian Aditya.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.tlsfree.core.config;
//
//import java.net.URI;
//import java.security.KeyPair;
//
//import javax.inject.Named;
//
//import org.shredzone.acme4j.Registration;
//import org.shredzone.acme4j.RegistrationBuilder;
//import org.shredzone.acme4j.Session;
//import org.shredzone.acme4j.exception.AcmeConflictException;
//import org.shredzone.acme4j.exception.AcmeException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//
//@Configuration
//public class ACMESessionConfig {
//	private final Logger log = LoggerFactory.getLogger(getClass());
//
//	private String acmeUrl;
//
//	public ACMESessionConfig(Environment env) {
//		this.acmeUrl = env.getRequiredProperty("acme.url");
//	}
//
//	@Bean
//	public Session acmeSession(@Named("acmeAccountKeyPair") KeyPair keyPair) {
//		return new Session(acmeUrl, keyPair);
//	}
//
//	@Bean
//	public Registration acmeRegistration(@Named("acmeSession") Session session) throws AcmeException {
//		Registration registration;
//
//		try {
//			log.info("Creating new Let's Encrypt account");
//			registration = new RegistrationBuilder().create(session);
//		} catch (AcmeConflictException ex) {
//			log.info("Let's Encrypt account already exists, binding existing one");
//			registration = Registration.bind(session, ex.getLocation());
//		}
//
//		URI agreement = registration.getAgreement();
//		registration.modify()
//			.setAgreement(agreement)
//			.commit();
//
//		return registration;
//	}
//}
