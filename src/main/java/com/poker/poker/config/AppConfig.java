package com.poker.poker.config;

import com.poker.poker.documents.GameDocument;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Data
@Configuration
public class AppConfig {

  @Bean
  public Map<UUID, GameDocument> activeGamesMap() {
    return new HashMap<>();
  }

  @Bean
  public Map<UUID, SseEmitter> gameEmittersMap() {
    return new HashMap<>();
  }

  @Bean
  public Set<UUID> playersInGamesSet() {
    return new HashSet<>();
  }
}
