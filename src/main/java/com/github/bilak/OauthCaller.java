package com.github.bilak;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author lvasek.
 */
public class OauthCaller {

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(20);
		try {
			for (int i = 0; i < 20; i++) {
				executorService.execute(() -> new OauthCaller().callAuth());
			}
		} finally {
			executorService.shutdown();
		}
	}

	public ResponseEntity<OAuth2AccessToken> callAuth() {

		RestOperations restOperations = new RestTemplate();

		Map<String, List<String>> headers = new LinkedHashMap<>();
		headers.put("Content-Type", Arrays.asList("application/x-www-form-urlencoded"));
		headers.put("authorization", Arrays.asList("Basic " + new String(Base64.encode("demo:demo".getBytes()))));

		UriComponentsBuilder builder = null;
		try {
			builder = UriComponentsBuilder
					.fromHttpUrl("http://localhost:9999/uaa/oauth/token")
					.queryParam("username", URLEncoder.encode("jdoe@domain.com", "UTF-8"))
					.queryParam("password", "password")
					.queryParam("grant_type", "password");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			return restOperations.exchange(new RequestEntity<>(new LinkedMultiValueMap<>(headers), HttpMethod.POST, builder.build(true).toUri()),
					OAuth2AccessToken.class);
		} catch (HttpStatusCodeException e) {
			e.printStackTrace();
			System.out.println("ERROR RESPONSE " + e.getResponseBodyAsString());
		}

		return null;

	}
}
