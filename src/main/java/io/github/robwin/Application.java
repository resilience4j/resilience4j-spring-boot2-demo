package io.github.robwin;


import io.github.resilience4j.fallback.CompletionStageFallbackDecorator;
import io.github.resilience4j.fallback.FallbackDecorator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public FallbackDecorator completionStageFallbackDecorator() {
		return new CompletionStageFallbackDecorator();
	}
}
