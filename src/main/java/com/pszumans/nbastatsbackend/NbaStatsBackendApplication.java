package com.pszumans.nbastatsbackend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class NbaStatsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NbaStatsBackendApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public CommandLineRunner init(RosterService rosterService) {
		return (args) -> {
			rosterService.setRosters();
		};
	}
}
