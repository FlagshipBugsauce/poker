package com.poker.poker.config;

import com.poker.poker.models.enums.UserGroup;
import java.util.List;
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
  @Value("${validation.admin-groups}")
  private final List<UserGroup> adminGroups;

  @Value("${validation.all-groups}")
  private final List<UserGroup> allGroups;

  @Value("${validation.general-groups}")
  private final List<UserGroup> generalGroups;

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

  @Value("${web-socket.topics.toast}")
  private String toastTopic;

  @Value("${web-socket.topics.secure}")
  private String secureTopic;

  @Value("${web-socket.topics.game-list}")
  private String gameListTopic;

  @Value("${web-socket.topics.game}")
  private String gameTopic;

  @Value("${web-socket.topics.current-game}")
  private String currentGameTopic;
}
