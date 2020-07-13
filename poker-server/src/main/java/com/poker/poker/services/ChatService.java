package com.poker.poker.services;

import com.poker.poker.events.ChatMessageEvent;
import com.poker.poker.events.PublishMessageEvent;
import com.poker.poker.events.SystemChatMessageEvent;
import com.poker.poker.models.websocket.ChatMessageModel;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ChatService {

  private final ApplicationEventPublisher applicationEventPublisher;

  /**
   * Handles a chat message event, where a user sends a message to a particular chat. Users can chat
   * in a general chat (on the home page) and also in a game chat.
   *
   * @param event ChatMessageEvent containing the message, author and game ID.
   */
  @EventListener
  public void handleChatMessageEvent(final ChatMessageEvent event) {
    final ChatMessageModel message =
        new ChatMessageModel(
            new Date(),
            event.getUser().getFirstName() + " " + event.getUser().getLastName(),
            event.getMessage());
    // If a game ID is provided, then broadcast to that game only, otherwise, broadcast to general.
    final String topic =
        event.getGameId() == null ? "/topic/chat/general" : "/topic/chat/" + event.getGameId();
    applicationEventPublisher.publishEvent(new PublishMessageEvent<>(this, topic, message));
  }

  /**
   * Handles a chat message event, where the system to sending some information to a chat box on
   * users clients.
   *
   * @param event SystemChatMessageEvent containing a message and game ID.
   */
  @EventListener
  public void handleSystemMessageEvent(final SystemChatMessageEvent event) {
    final ChatMessageModel message = new ChatMessageModel(new Date(), null, event.getMessage());
    // If a game ID is provided, then broadcast to that game only, otherwise, broadcast to general.
    final String topic =
        event.getGameId() == null ? "/topic/chat/general" : "/topic/chat/" + event.getGameId();
    applicationEventPublisher.publishEvent(new PublishMessageEvent<>(this, topic, message));
  }
}
