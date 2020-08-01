package com.poker.poker.models;

import com.poker.poker.documents.UserDocument;
import com.poker.poker.models.enums.CardSuit;
import com.poker.poker.models.enums.CardValue;
import com.poker.poker.models.enums.GamePhase;
import com.poker.poker.models.enums.MessageType;
import com.poker.poker.models.enums.UserGroup;
import com.poker.poker.models.game.ActiveStatusModel;
import com.poker.poker.models.game.CardModel;
import com.poker.poker.models.game.DeckModel;
import com.poker.poker.models.game.DrawGameDataContainerModel;
import com.poker.poker.models.game.DrawGameDrawModel;
import com.poker.poker.models.game.GameListModel;
import com.poker.poker.models.game.GameModel;
import com.poker.poker.models.game.GameParameterModel;
import com.poker.poker.models.game.GamePlayerModel;
import com.poker.poker.models.game.HandSummaryModel;
import com.poker.poker.models.game.LobbyModel;
import com.poker.poker.models.game.LobbyPlayerModel;
import com.poker.poker.models.game.PlayerModel;
import com.poker.poker.models.game.PokerTableModel;
import com.poker.poker.models.game.TimerModel;
import com.poker.poker.models.user.AuthRequestModel;
import com.poker.poker.models.user.AuthResponseModel;
import com.poker.poker.models.user.JwtAuthRequestModel;
import com.poker.poker.models.user.NewAccountModel;
import com.poker.poker.models.user.UserModel;
import com.poker.poker.models.websocket.ChatMessageModel;
import com.poker.poker.models.websocket.ClientMessageModel;
import com.poker.poker.models.websocket.CurrentGameModel;
import com.poker.poker.models.websocket.GenericServerMessage;
import com.poker.poker.models.websocket.ToastClassModel;
import com.poker.poker.models.websocket.ToastModel;
import com.poker.poker.models.websocket.WebSocketInfoModel;
import com.poker.poker.models.websocket.WebSocketUpdateModel;
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
public class ModelModel {

  // Enums
  private CardSuit cardSuit;
  private CardValue cardValue;
  private GamePhase gamePhase;
  private MessageType messageType;
  private UserGroup userGroup;

  // Game Models
  private ActiveStatusModel activeStatusModel;
  private CardModel cardModel;
  private DeckModel deckModel;
  private DrawGameDataContainerModel drawGameDataContainerModel;
  private DrawGameDrawModel drawGameDrawModel;
  private GameListModel gameListModel;
  private GameModel gameModel;
  private GameParameterModel gameParameterModel;
  private GamePlayerModel gamePlayerModel;
  private HandSummaryModel handSummaryModel;
  private LobbyModel lobbyModel;
  private LobbyPlayerModel lobbyPlayerModel;
  private PlayerModel playerModel;
  private PokerTableModel pokerTableModel;
  private TimerModel timerModel;

  // User Models
  private UserDocument userDocument;
  private UserModel userModel;
  private NewAccountModel newAccountModel;
  private AuthRequestModel authRequestModel;
  private AuthResponseModel authResponseModel;
  private JwtAuthRequestModel jwtAuthRequestModel;

  // WebSocket Models
  private ChatMessageModel chatMessageModel;
  private ClientMessageModel clientMessageModel;
  private CurrentGameModel currentGameModel;
  private GenericServerMessage genericServerMessage;
  private ToastClassModel toastClassModel;
  private ToastModel toastModel;
  private WebSocketInfoModel webSocketInfoModel;
  private WebSocketUpdateModel webSocketUpdateModel;

  // General Models
  private ApiErrorModel apiErrorModel;
  private ApiSuccessModel apiSuccessModel;
}
