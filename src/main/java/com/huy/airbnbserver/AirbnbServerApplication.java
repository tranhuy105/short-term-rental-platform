package com.huy.airbnbserver;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AirbnbServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AirbnbServerApplication.class, args);
    }


    @Bean
    public OpenAPI airbnbServerAPI() {
        return new OpenAPI()
                .info(new Info().title("AirBnb Clone Server API")
                        .description("A Quick Documentation For This REST API")
                        .version("0.0.1")
                        .license(new License().name("Apache 2.0"))
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("You can refer to the complete project source code here")
                                .url("https://github.com/tranhuy105/short-term-rental-platform")
                );
    }
}
