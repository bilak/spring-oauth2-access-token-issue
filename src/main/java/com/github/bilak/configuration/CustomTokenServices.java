package com.github.bilak.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

/**
 * @author lvasek.
 */
public class CustomTokenServices extends DefaultTokenServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomTokenServices.class);

	@Override
	@Retryable(include = DuplicateKeyException.class)
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
		try {
			return super.createAccessToken(authentication);
		} catch (DuplicateKeyException ex) {
			LOGGER.info(String.format("DuplicateKeyException while creating access token %s", ex));
			throw ex;
		} catch (Exception ex) {
			LOGGER.info(String.format("Exception while creating access token %s", ex));
			throw new RuntimeException(ex);
		}
	}

	@Recover
	public OAuth2AccessToken recoverAccessToken(OAuth2Authentication authentication)
			throws AuthenticationException {
		try {
			return getAccessToken(authentication);
		} catch (Exception ex) {
			LOGGER.error("Unable to recover from createAccessToken error", ex);
		}
		throw new IllegalStateException("Cannot create access token");
	}
}