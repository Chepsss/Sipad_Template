package it.almaviva.difesa.template.shared.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeneralApplicationConfig {

    @Bean
    public WebClient.Builder myWebClientBuilder() {
        return WebClient.builder();
    }
}
