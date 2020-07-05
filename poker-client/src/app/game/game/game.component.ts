import {AfterViewInit, Component, HostListener, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DrawGameDataModel, GameDocument} from 'src/app/api/models';
import {LobbyComponent} from '../lobby/lobby.component';
import {LeaveGameGuardService} from './leave-game-guard.service';
import {PopupComponent, PopupContentModel} from 'src/app/shared/popup/popup.component';
import {GameState} from 'src/app/shared/models/game-state.enum';
import {PlayComponent} from '../play/play.component';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameStateContainer
} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {
  selectGameData,
  selectGameDocument,
  selectGameState,
  selectJwt
} from '../../state/app.selector';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {leaveGame, leaveLobby} from '../../state/app.actions';
import {WebSocketService} from '../../shared/web-socket.service';
import {MessageType} from '../../shared/models/message-types.enum';

@Component({
  selector: 'pkr-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, AfterViewInit, OnDestroy {

  /** Reference to the lobby component. */
  @ViewChild(LobbyComponent) lobbyComponent: LobbyComponent;
  /** Reference to the play component. */
  @ViewChild(PlayComponent) playComponent: PlayComponent;
  /**
   * Popup that will appear if a player clicks on a link to warn them that they will be removed
   * from the game if they navigate to another page.
   */
  @ViewChild('popup') public confirmationPopup: PopupComponent;
  /** A summary of what occurred in the game. */
  public gameData: DrawGameDataModel[] = [] as DrawGameDataModel[];
  /**
   * Content for the popup that appears when leaving the page (except when refreshing or going to
   * external site).
   */
  public popupContent: PopupContentModel[] = [
    {body: 'You will be removed from the game if you leave this page.'} as PopupContentModel,
    {body: 'Click Ok to continue.'} as PopupContentModel
  ] as PopupContentModel[];
  /** Helper subject which assists in terminating subscriptions. */
  public ngDestroyed$ = new Subject();
  /** The model representing the state of the game at any given point in time. */
  public gameModel: GameDocument;
  /** The last state the game was in. */
  private lastState: string = '';
  private jwt: string;

  constructor(
    private leaveGameGuardService: LeaveGameGuardService,
    private appStore: Store<AppStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private gameDataStore: Store<GameDataStateContainer>,
    private webSocketService: WebSocketService) {
  }

  public ngAfterViewInit(): void {
    this.leaveGameGuardService.confirmationPopup = this.confirmationPopup;
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
  }

  public ngOnInit(): void {
    this.appStore.select(selectJwt)
    .pipe(takeUntil(this.ngDestroyed$)).subscribe(jwt => this.jwt = jwt);
    this.leaveGameGuardService.canLeave = false;  // Need to set this to false when page loads.
    this.gameStore.select(selectGameDocument).pipe(takeUntil(this.ngDestroyed$)).subscribe(
      (game: GameDocument) => this.gameModel = game);

    this.gameStore.select(selectGameState)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe(state => {
      if (state !== this.lastState && state === 'Play') {
        this.webSocketService.subscribeToPlayerDataTopic();
        this.webSocketService.requestGameTopicUpdate(MessageType.GameData);
        this.webSocketService.requestGameTopicUpdate(MessageType.Hand);
      }
      this.lastState = state;
    });

    this.gameDataStore.select(selectGameData).pipe(takeUntil(this.ngDestroyed$)).subscribe(
      (data: DrawGameDataModel[]) => this.gameData = data
    );
  }

  /**
   * Ensures that the user "leaves" the game if they leave the page (by refreshing, closing tab, or
   * going to another website).
   */
  @HostListener('window:beforeunload', ['$event'])
  public userLeftPage($event: any): void {
    if (this.gameModel.state === GameState.Lobby) {
      this.appStore.dispatch(leaveLobby());
    }
    if (this.gameModel.state === GameState.Play) {
      this.appStore.dispatch(leaveGame({jwt: this.jwt, actionType: 'LeaveGame'}));
    }
  }
}
