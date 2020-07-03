package com.poker.poker.services;

import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.WebSocketInfoModel;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class WebSocketService {

  private final Map<UUID, WebSocketInfoModel> privateSockets;

  private final SimpMessagingTemplate template;

  // Need a method that runs every 10 or so minutes to get rid of inactive entries
  @Scheduled(cron = "0 0 0/1 * * ?") // Runs at the start of every minute
  public void cleanPrivateSockets() {
    log.debug("Performing private sockets cleanup task.");
    for (final UUID id : privateSockets.keySet()) {
      // If last activity is more than an hour ago (or whatever) then remove.
    }
  }

  // Need a method to create a new entry in the map
  public UUID requestPrivateTopic(final UserDocument user) {
    final UUID privateId = UUID.randomUUID();
    privateSockets.put(user.getId(), new WebSocketInfoModel(privateId, new Date()));
    return privateId;
  }

  // Need a method to remove an entry from the map
  public void removePrivateTopic(final UserDocument user) {
    // TODO: Add validation
    privateSockets.remove(user.getId());
  }

  public void sendPrivateMessage(final UUID recipient, final Object data) {
    // TODO: Add validation
    final WebSocketInfoModel model = privateSockets.get(recipient);
    template.convertAndSend("/topic/secure/" + model.getSecureTopicId(), data);
    model.setLastActivity(new Date());
  }

  public void sendPublicMessage(final String topic, final Object data) {
    template.convertAndSend(topic, data);
  }
}
