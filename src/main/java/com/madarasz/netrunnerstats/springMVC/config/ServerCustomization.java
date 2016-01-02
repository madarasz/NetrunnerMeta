package com.madarasz.netrunnerstats.springMVC.config;

import com.madarasz.netrunnerstats.helper.dialect.KTMDialect;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

/**
 * Created by madarasz on 11/30/15.
 * Server configuration class.
 */
@Configuration
public class ServerCustomization extends ServerProperties {

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        super.customize(container);
        container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404.html"));
    }

    // custom Thymeleaf dialect
    @Bean
    public KTMDialect ktmDialect() {
        return new KTMDialect();
    }
}
