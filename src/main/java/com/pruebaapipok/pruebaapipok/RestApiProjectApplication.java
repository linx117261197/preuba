package com.pruebaapipok.pruebaapipok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class RestApiProjectApplication {

	 public static void main(String[] args) {
	        SpringApplication.run(RestApiProjectApplication.class, args);
	    }

	    @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }
	}
	