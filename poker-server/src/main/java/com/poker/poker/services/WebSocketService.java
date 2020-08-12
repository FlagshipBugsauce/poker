package com.poker.poker.services;

import com.poker.poker.config.AppConfig;
import com.poker.poker.events.CurrentGameEvent;
import com.poker.poker.events.GameMessageEvent;
import com.poker.poker.events.PrivateMessageEvent;
import com.poker.poker.events.PublishMessageEvent;
import com.poker.poker.events.ToastMessageEvent;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.user.UserModel;
import com.poker.poker.models.websocket.GenericServerMessage;
import com.poker.poker.models.websocket.PrivateTopicModel;
import com.poker.poker.models.websocket.ToastClassModel;
import com.poker.poker.models.websocket.ToastModel;
import com.poker.poker.models.websocket.WebSocketInfoModel;
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
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
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
  public PrivateTopicModel requestPrivateTopic(final UserModel user) {
    final UUID privateId = UUID.randomUUID();
    privateSockets.put(user.getId(), new WebSocketInfoModel(privateId, new Date()));
    return new PrivateTopicModel(privateId);
  }

  /**
   * Manually removes a private topic.
   *
   * @param user The ID of the user the topic is used to communicate with.
   */
  public void removePrivateTopic(final UserModel user) {
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
  public <T> void sendPrivateMessage(final UUID recipient, final GenericServerMessage<T> data) {
    // TODO: Add validation
    final WebSocketInfoModel model = privateSockets.get(recipient);
    template.convertAndSend("/topic/secure/" + model.getSecureTopicId(), data);
    model.setLastActivity(new Date());
  }

  @EventListener
  public <T> void privateMessageEventHandler(final PrivateMessageEvent<T> event) {
    final WebSocketInfoModel socketInfo = privateSockets.get(event.getId());
    if (socketInfo == null) {
      log.debug("Player with ID {} has no private socket.", event.getId());
      return;
    }
    final String topic = "/topic/secure/" + socketInfo.getSecureTopicId();
    socketInfo.setLastActivity(new Date());
    template.convertAndSend(topic, event.getMessage());
  }

  /**
   * Sends the provided data to the specified topic. This message is public and no attempt has been
   * made to conceal the topic from anyone who wants to listen in.
   *
   * @param topic Topic to broadcast to.
   * @param data Data being broadcast.
   * @param <T> Type of data.
   */
  public <T> void sendPublicMessage(final String topic, final GenericServerMessage<T> data) {
    template.convertAndSend(topic, data);
  }

  /**
   * Handles message events that are destined for the /topic/game/{id} topic. These could be
   * messages that are being sent to a game ID, or a player ID.
   *
   * @param event Message event.
   * @param <T> Data type.
   */
  @EventListener
  public <T> void gameMessageEventHandler(final GameMessageEvent<T> event) {
    template.convertAndSend(appConfig.getGameTopic() + event.getId(), event.getMessage());
  }

  @EventListener
  public void toastMessageEventHandler(final ToastMessageEvent event) {
    template.convertAndSend(
        appConfig.getGameTopic() + event.getId(),
        new GenericServerMessage<>(MessageType.Toast, event.getToast()));
  }

  /**
   * Handles publish message events. Unlike sendPublicMessage, this method will broadcast raw data,
   * as in, it will not place the data in a container with a type enum indicating what kind of data
   * is being sent.
   *
   * @param publishMessageEvent Event that is published when server should send raw data.
   * @param <T> Type of data being sent.
   */
  @EventListener
  public <T> void messageEventHandler(final PublishMessageEvent<T> publishMessageEvent) {
    template.convertAndSend(publishMessageEvent.getTopic(), publishMessageEvent.getData());
    log.debug("Sent generic message to topic {}.", publishMessageEvent.getTopic());
  }

  /**
   * Helper which will broadcast a message to the client which will appear as a toast. Note that
   * this method will only display toasts with a specific appearance (light bg, medium size, which
   * last for 5 seconds).
   *
   * @param gameId The ID of the lobby the toast should appear in.
   * @param message The message that should be contained in the toast.
   * @param size The size of the toast (sm, md, lg, xl).
   */
  public void sendGameToast(final UUID gameId, final String message, final String size) {
    sendPublicMessage(
        appConfig.getGameTopic() + gameId,
        new GenericServerMessage<>(
            MessageType.Toast,
            new ToastModel(message, new ToastClassModel("bg-light toast-" + size, 5000))));
  }

  @EventListener
  public void updateCurrentGameForUser(final CurrentGameEvent currentGameEvent) {
    template.convertAndSend(
        appConfig.getCurrentGameTopic() + currentGameEvent.getUserId(),
        currentGameEvent.getCurrentGameModel());
  }
}
