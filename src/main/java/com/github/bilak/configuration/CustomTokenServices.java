package com.github.bilak.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lvasek.
 */
public class CustomTokenServices extends DefaultTokenServices {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomTokenServices.class);

	@Override
	@Transactional(isolation = Isolation.DEFAULT)
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
		try {
			return super.createAccessToken(authentication);
		} catch (DuplicateKeyException dke) {
			LOGGER.info(String.format("Duplicate user found for %s", authentication.getUserAuthentication().getPrincipal()));
			return super.getAccessToken(authentication);
		} catch (Exception ex) {
			LOGGER.info(String.format("Exception while creating access token %s", ex));
		}
		return null;
	}
}