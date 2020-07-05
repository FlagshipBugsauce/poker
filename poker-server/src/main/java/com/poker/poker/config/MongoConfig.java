package com.poker.poker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = {"com.poker.poker.repositories"})
@Configuration
public class MongoConfig {

}
