import {AfterViewInit, Component, HostListener, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {DrawGameDataModel, GameModel} from 'src/app/api/models';
import {LobbyComponent} from '../lobby/lobby.component';
import {LeaveGameGuardService} from './leave-game-guard.service';
import {PopupComponent, PopupContentModel} from 'src/app/shared/popup/popup.component';
import {GamePhase} from 'src/app/shared/models/game-phase.enum';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameStateContainer
} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {selectGameData, selectGameModel, selectGamePhase} from '../../state/app.selector';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {leaveGame, leaveLobby, unsubscribeFromGameTopics} from '../../state/app.actions';
import {WebSocketService} from '../../shared/web-socket/web-socket.service';
import {Router} from '@angular/router';

@Component({
  selector: 'pkr-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, AfterViewInit, OnDestroy {

  /** Reference to the lobby component. */
  @ViewChild(LobbyComponent) lobbyComponent: LobbyComponent;
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
  public gameModel: GameModel;
  public phase: GamePhase = GamePhase.Lobby;

  constructor(
    private leaveGameGuardService: LeaveGameGuardService,
    private appStore: Store<AppStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private gameDataStore: Store<GameDataStateContainer>,
    private webSocketService: WebSocketService,
    private router: Router) {
  }

  /** Getter for game ID which uses active router link. */
  public get gameId(): string {
    return this.router.url.split('/')[2];
  }

  public ngAfterViewInit(): void {
    this.leaveGameGuardService.confirmationPopup = this.confirmationPopup;
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
    this.gameStore.dispatch(unsubscribeFromGameTopics());
  }

  public ngOnInit(): void {
    this.leaveGameGuardService.canLeave = false;  // Need to set this to false when page loads.
    this.gameStore.select(selectGameModel).pipe(takeUntil(this.ngDestroyed$)).subscribe(
      (game: GameModel) => this.gameModel = game);

    this.gameStore.select(selectGamePhase)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((phase: GamePhase) => this.phase = phase);

    this.gameDataStore.select(selectGameData).pipe(takeUntil(this.ngDestroyed$)).subscribe(
      (data: DrawGameDataModel[]) => this.gameData = data ? data : this.gameData);
  }

  /**
   * Ensures that the user "leaves" the game if they leave the page (by refreshing, closing tab, or
   * going to another website).
   */
  @HostListener('window:beforeunload', ['$event'])
  public userLeftPage($event: any): void {
    if (this.gameModel.phase === GamePhase.Lobby) {
      this.appStore.dispatch(leaveLobby());
    }
    if (this.gameModel.phase === GamePhase.Play) {
      this.appStore.dispatch(leaveGame());
    }
  }
}
