package com.github.bilak;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {	SpringOauth2AccessTokenIssueApplication.class },
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
		properties = {"spring.profiles.active=jpapostgre-dev"})
public class SpringOauth2AccessTokenIssueApplicationTests {

	ExecutorService executorService;

	@Before
	public void setup() {
		executorService = Executors.newFixedThreadPool(20);
	}

	@After
	public void tearDown() {
		executorService.shutdown();
	}

	@Test
	public void testAuthPerformance() {
		int numberOfCalls = 400;
		List<CompletableFuture<ResponseEntity<OAuth2AccessToken>>> tasksArray = new ArrayList<>(
				numberOfCalls);

		for (int i = 0; i < numberOfCalls; i++) {
			tasksArray.add(CompletableFuture.supplyAsync(() -> {
				try {
					TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(200));
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					throw new IllegalStateException("Interrupted");
				}
				return new OauthCaller().callAuth();
			}, executorService));
		}

		for (CompletableFuture<ResponseEntity<OAuth2AccessToken>> task : tasksArray) {
			ResponseEntity<OAuth2AccessToken> responseEntity = task.join();
			assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		}
	}

}
