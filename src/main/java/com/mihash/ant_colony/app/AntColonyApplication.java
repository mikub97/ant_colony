package com.mihash.ant_colony.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Arrays;

@SpringBootApplication//(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableMongoRepositories({"com.mihash.ant_colony.repositories"})
@ComponentScan(basePackages = {"com.mihash.ant_colony.controllers"})
public class AntColonyApplication {


	public static void main(String[] args) {
		SpringApplication.run(AntColonyApplication.class, args);
		
	}

}
