package com.poker.poker.services;

import com.poker.poker.config.AppConfig;
import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.SocketContainerModel;
import com.poker.poker.models.WebSocketInfoModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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

  private final AppConfig appConfig;

  /**
   * A mapping of user ID's to WebSocketInfoModels. The info models contain an ID that is used to
   * compose a topic that only one user will know about. This allows the application to broadcast
   * private data to a specific user.
   */
  private final Map<UUID, WebSocketInfoModel> privateSockets;

  private final SimpMessagingTemplate template;

  /**
   * Scheduled task that will clean up records of private topics if nothing has been sent for the
   * number of hours specified in poker.properties.
   */
  @Scheduled(cron = "0 0 0/1 * * ?") // Runs at the start of every minute
  public void cleanPrivateSockets() {
    log.debug("Performing private sockets cleanup task.");
    final List<UUID> modelsToRemove = new ArrayList<>();
    for (final UUID id : privateSockets.keySet()) {
      // If last activity is more than an hour ago (or whatever) then remove.
      final DateTime lastActivity = new DateTime(privateSockets.get(id).getLastActivity());
      if (lastActivity.isBefore(
          DateTime.now().minusHours(appConfig.getPrivateSocketTimeoutHours()))) {
        modelsToRemove.add(id);
      }
    }
    for (final UUID id : modelsToRemove) {
      privateSockets.remove(id);
    }
  }

  /**
   * Creates a UUID associated with a particular user that will be used to communicate private
   * information to that user, such as the player's cards in a hand of poker. Using a UUID instead
   * of a JWT since a JWT is needlessly long. The topic will be /topic/secure/{id}
   *
   * @param user The user requesting a private topic.
   * @return The UUID component of the topic.
   */
  public UUID requestPrivateTopic(final UserDocument user) {
    final UUID privateId = UUID.randomUUID();
    privateSockets.put(user.getId(), new WebSocketInfoModel(privateId, new Date()));
    return privateId;
  }

  /**
   * Manually removes a private topic.
   *
   * @param user The ID of the user the topic is used to communicate with.
   */
  public void removePrivateTopic(final UserDocument user) {
    // TODO: Add validation
    privateSockets.remove(user.getId());
  }

  /**
   * Sends a private message to the user with the specified ID, provided they have requested a
   * secure topic to communicate on.
   *
   * @param recipient The ID of the user.
   * @param data The data to be sent to this user.
   */
  public void sendPrivateMessage(final UUID recipient, final SocketContainerModel data) {
    // TODO: Add validation
    final WebSocketInfoModel model = privateSockets.get(recipient);
    template.convertAndSend("/topic/secure/" + model.getSecureTopicId(), data);
    model.setLastActivity(new Date());
  }

  /**
   * Sends the providied data to the specified topic. This message is public and no attempt has been
   * made to conceal the topic from anyone who wants to listen in.
   *
   * @param topic The topic to broadcast to.
   * @param data The data to broadcast.
   */
  public void sendPublicMessage(final String topic, final SocketContainerModel data) {
    template.convertAndSend(topic, data);
    log.debug("Sent {} update to topic {}.", data.getType(), topic);
  }
}
