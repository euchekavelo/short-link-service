package ru.promo_z.shortlinkservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Сервис коротких ссылок",
                description = "Описание спецификации API сервиса по генерации коротких ссылок.",
                version = "1.0.0",
                contact = @Contact(
                        name = "Suslov Kirill",
                        url = "https://t.me/euchekavelo"
                )
        )
)
public class OpenApiConfig {
}
