package com.poker.poker.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@Data
@EnableAsync
@Configuration
@PropertySource("classpath:poker.properties")
public class AppConfig {
  @Value("${min-number-of-players}")
  private int minNumberOfPlayers;

  @Value("${max-number-of-players}")
  private int maxNumberOfPlayers;

  @Value("${num-rounds-in-roll-game}")
  private int numRoundsInRollGame;

  @Value("${time-to-act-in-millis}")
  private int timeToActInMillis;

  @Value("${web-socket.private-socket-timeout-hours}")
  private int privateSocketTimeoutHours;
}
