package com.github.bilak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class SpringOauth2AccessTokenIssueApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringOauth2AccessTokenIssueApplication.class, args);
	}

	//@Service
	public static class Initializer implements ApplicationListener<ApplicationReadyEvent> {

		@Override
		public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
			ExecutorService executorService = Executors.newFixedThreadPool(10);
			try {
				for (int i = 0; i < 10; i++) {
					executorService.execute(() -> new OauthCaller().callAuth());

				}
			} finally {
				executorService.shutdown();
			}
		}
	}

}
