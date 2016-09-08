package com.rybak.tool.similarity;

import com.rybak.tool.similarity.storege.StorageProperties;
import com.rybak.tool.similarity.service.ImageComparisonService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(ImageComparisonService storageService) {
		return (args) -> {
            storageService.deleteAll();
            storageService.init();
		};
	}
}
