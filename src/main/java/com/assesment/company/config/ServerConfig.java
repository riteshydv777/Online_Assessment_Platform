package com.assesment.company.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

@Configuration
public class ServerConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> {
            factory.setContextPath("");
            factory.setPort(8080);
            factory.addConnectorCustomizers((Connector connector) -> {
                connector.setProperty("maxThreads", "200");
                connector.setProperty("minSpareThreads", "10");
                connector.setProperty("acceptCount", "100");
                connector.setProperty("connectionTimeout", "20000");
            });
        };
    }
} 