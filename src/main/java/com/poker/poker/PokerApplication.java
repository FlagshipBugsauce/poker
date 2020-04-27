package com.poker.poker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PokerApplication {

  public static void main(String[] args) {
    SpringApplication.run(PokerApplication.class, args);
  }
}
