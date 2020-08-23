package com.poker.poker.models;

import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import com.poker.poker.models.enums.GameAction;
import com.poker.poker.models.enums.GamePhase;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.models.game.ActiveStatus;
import com.poker.poker.models.game.Card;
import com.poker.poker.models.game.CurrentGame;
import com.poker.poker.models.game.Deal;
import com.poker.poker.models.game.Deck;
import com.poker.poker.models.game.DrawGameDataContainer;
import com.poker.poker.models.game.DrawGameDraw;
import com.poker.poker.models.game.Game;
import com.poker.poker.models.game.GameActionData;
import com.poker.poker.models.game.GameList;
import com.poker.poker.models.game.GameParameter;
import com.poker.poker.models.game.GamePlayer;
import com.poker.poker.models.game.HandSummary;
import com.poker.poker.models.game.HideCards;
import com.poker.poker.models.game.Lobby;
import com.poker.poker.models.game.LobbyPlayer;
import com.poker.poker.models.game.Player;
import com.poker.poker.models.game.PokerTable;
import com.poker.poker.models.game.Pot;
import com.poker.poker.models.game.TableControls;
import com.poker.poker.models.game.Timer;
import com.poker.poker.models.game.Winner;
import com.poker.poker.models.user.AuthRequest;
import com.poker.poker.models.user.AuthResponse;
import com.poker.poker.models.user.ClientUser;
import com.poker.poker.models.user.JwtAuthRequest;
import com.poker.poker.models.user.NewAccount;
import com.poker.poker.models.user.User;
import com.poker.poker.models.websocket.ChatMessage;
import com.poker.poker.models.websocket.ClientMessage;
import com.poker.poker.models.websocket.GenericServerMessage;
import com.poker.poker.models.websocket.PrivateTopic;
import com.poker.poker.models.websocket.Toast;
import com.poker.poker.models.websocket.ToastClass;
import com.poker.poker.models.websocket.WebSocketInfo;
import com.poker.poker.models.websocket.WebSocketUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model that has fields with every model to help with automatic client model generation. By having
 * one endpoint that returns this model, all models used by fields in this model will be generated
 * automatically.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Models {

  // Enums
  private CardSuit cardSuit;
  private CardValue cardValue;
  private GameAction gameAction;
  private GamePhase gamePhase;
  private MessageType messageType;
  private UserGroup userGroup;

  // Game Models
  private ActiveStatus activeStatus;
  private Card card;
  private Deal deal;
  private Deck deck;
  private DrawGameDataContainer drawGameDataContainer;
  private DrawGameDraw drawGameDrawModel;
  private GameActionData gameActionData;
  private GameList gameList;
  private Game game;
  private GameParameter gameParameter;
  private GamePlayer gamePlayer;
  private HandSummary handSummary;
  private HideCards hideCards;
  private Lobby lobby;
  private LobbyPlayer lobbyPlayer;
  private Player player;
  private PokerTable pokerTable;
  private Pot pot;
  private Timer timer;
  private TableControls tableControls;
  private Winner winner;

  // User Models
  private User user;
  private ClientUser clientUser;
  private NewAccount newAccount;
  private AuthRequest authRequest;
  private AuthResponse authResponse;
  private JwtAuthRequest jwtAuthRequest;

  // WebSocket Models
  private ChatMessage chatMessage;
  private ClientMessage clientMessage;
  private CurrentGame currentGame;
  private GenericServerMessage genericServerMessage;
  private PrivateTopic privateTopic;
  private ToastClass toastClass;
  private Toast toast;
  private WebSocketInfo webSocketInfo;
  private WebSocketUpdate webSocketUpdate;

  // General Models
  private ApiError apiError;
  private ApiSuccess apiSuccess;
}
