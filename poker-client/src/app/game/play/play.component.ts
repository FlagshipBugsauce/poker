import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {
  DrawGameDataModel,
  GameDocument,
  HandDocument,
  UserModel,
} from '../../api/models';
import {ToastService} from '../../shared/toast.service';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameStateContainer,
  HandStateContainer, PlayerDataStateContainer
} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {drawCard, setAwayStatus} from '../../state/app.actions';
import {
  selectActingStatus, selectAwayStatus,
  selectGameData,
  selectGameDocument,
  selectHandDocument,
  selectLoggedInUser, selectPlayerData,
} from '../../state/app.selector';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';
import {WebSocketService} from '../../shared/web-socket.service';

@Component({
  selector: 'pkr-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.scss']
})
export class PlayComponent implements OnInit, OnDestroy {
  @ViewChild('afkPopup') afkPopup: PopupAfkComponent;

  constructor(
    private appStore: Store<AppStateContainer>,
    private gameDataStore: Store<GameDataStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private handStore: Store<HandStateContainer>,
    private playerDataStore: Store<PlayerDataStateContainer>,
    private toastService: ToastService,
    private webSocketService: WebSocketService) {
  }

  /**
   * Path to AFK icon.
   */
  public activeIcon: string = 'assets/icons/afk.svg';

  /**
   * Model of the user currently logged in.
   */
  private user: UserModel;

  /**
   * Time remaining for a player to act (when applicable).
   */
  public timeToAct: number = 0;

  /**
   * Numbers used for the table summarizing what has occurred in the game.
   */
  public numbers: number[] = [];

  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject();

  /**
   * Getter for the model representing the current hand.
   */
  public hand: HandDocument;

  /**
   * Getter for the model representing the current game.
   */
  public gameModel: GameDocument;

  /**
   * Getter for the data representing the current game.
   */
  public gameData: DrawGameDataModel[];

  /**
   * Flag that informs the UI when a player is able to perform a roll action.
   */
  public canRoll: boolean;

  /**
   * Flag used to track whether a player is AFK or not.
   */
  public awayStatus: boolean;

  public ngOnDestroy() {
    this.ngDestroyed$.next();
  }

  public ngOnInit(): void {
    this.appStore.select(selectLoggedInUser).subscribe(user => this.user = user);

    this.gameDataStore.select(selectGameData)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((data: DrawGameDataModel[]) => {
        // TODO: Investigate why data is sometimes undefined.
        this.gameData = data ? data : this.gameData;
        if (this.numbers.length === 0 && data && data[0] && data[0].draws) {
          this.numbers = Array(data[0].draws.length).fill('').map((v, i) => i + 1);
        }
      });

    this.gameStore.select(selectGameDocument)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((gameDocument: GameDocument) =>  this.gameModel = gameDocument);

    this.handStore.select(selectHandDocument)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((handDocument: HandDocument) => {
        this.hand = handDocument;

        if (this.hand.playerToAct && this.hand.playerToAct.id) {
          // Display toast
          const lastAction = this.hand.actions != null ? this.hand.actions[this.hand.actions.length - 1] : null;
          if (lastAction != null) {
            this.toastService.show(lastAction.message, {classname: 'bg-light toast-lg', delay: 5000});
          }
          if (this.gameModel.state !== 'Over') {
            this.startTurnTimer().then();
          }
        }
      });

    this.playerDataStore.select(selectActingStatus)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((acting: boolean) => this.canRoll = acting);

    this.playerDataStore.select(selectAwayStatus)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((status: boolean) => {
      this.awayStatus = status;
      if (this.awayStatus && this.afkPopup) {
        this.afkPopup.open();
      }
    });
  }

  /**
   * Toggles the players away status.
   */
  public toggleStatus(): void {
    this.playerDataStore.dispatch(setAwayStatus({away: !this.awayStatus}));
    if (this.canRoll) {
      this.draw();
    }
  }

  /**
   * Checks if player with specified ID is active.
   * @param id ID being checked.
   */
  public isPlayerAway(id: string): boolean {
    return this.gameModel.players.filter(player => player.id === id)[0].away;
  }

  /**
   * Draws a card from the deck.
   */
  public draw(): void {
    this.appStore.dispatch(drawCard());
  }

  /**
   * Begins a timer which will display how much time a player has to perform an action, before the action is performed for them.
   */
  private async startTurnTimer(): Promise<void> {
    const numHandActions = this.hand.actions.length;
    let currentTime = this.gameModel.timeToAct;
    while (currentTime >= 0) {
      this.timeToAct = currentTime;
      await new Promise(resolve => setTimeout(resolve, 1000));
      if (this.hand && this.hand.actions && this.hand.actions.length !== numHandActions) {
        break;
      }
      this.timeToAct = --currentTime;
    }
  }
}
