package com.madarasz.netrunnerstats;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.config.Neo4jConfiguration;

/**
 * Main application class
 * Created by madarasz on 2015-06-08.
 */
@SpringBootApplication
@ConfigurationProperties(prefix = "maindb")
public class Application extends SpringBootServletInitializer {

    private static Class<Application> applicationClass = Application.class;

    @Configuration
    @EnableNeo4jRepositories(basePackages = "com.madarasz.netrunnerstats")
    static class ApplicationConfig extends Neo4jConfiguration {

        public ApplicationConfig() {
            setBasePackage("com.madarasz.netrunnerstats");
        }

        @Bean
        @Primary
        @ConfigurationProperties(prefix = "maindb")
        GraphDatabaseService graphDatabaseService() {
            return new GraphDatabaseFactory().newEmbeddedDatabase("netrunner.db");
        }

        @Bean
        @ConfigurationProperties(prefix = "testdb")
        GraphDatabaseService graphTestDatabaseService() {
            return new GraphDatabaseFactory().newEmbeddedDatabase("test.db");
        }
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(applicationClass);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(applicationClass, args);
    }
}
