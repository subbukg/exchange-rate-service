package com.bv.exchange.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Exchange Rate Service!")
                                .contact(new Contact().email("subramanya.kg@gmail.com"))
                                .description(
                                        "For a given source and/or target currency(ies) get the exchange. Also, compute the value conversion for a given source and set of target currencies.")
                                .version("0.0.1"));
    }
}
