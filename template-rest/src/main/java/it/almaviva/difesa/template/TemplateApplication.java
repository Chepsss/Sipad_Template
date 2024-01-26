package it.almaviva.difesa.template;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;


@SpringBootApplication
@EntityScan(basePackages = "it.almaviva.difesa")
@ComponentScan(basePackages = "it.almaviva.difesa")
public class TemplateApplication {


	@Value("${ms.sipad.base.url}")
	private String msSipadBaseUrl;

	public static void main(String[] args) {
		SpringApplication.run(TemplateApplication.class, args);
	}


	@Bean
	public WebClient sipadClient() {
		return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder().codecs(
				clientCodecConfigurer ->
						clientCodecConfigurer.defaultCodecs().maxInMemorySize(Integer.MAX_VALUE)).build()).baseUrl(msSipadBaseUrl).build();
	}

}
