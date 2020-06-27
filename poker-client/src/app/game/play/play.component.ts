import {Component, OnDestroy, OnInit} from '@angular/core';
import {SseService} from 'src/app/shared/sse.service';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {
  DrawGameDataModel,
  GameDocument,
  HandDocument, UserModel,
} from '../../api/models';
import {ToastService} from '../../shared/toast.service';
import {GameState} from '../../shared/models/game-state.enum';
import {
  AppStateContainer,
  GameDataStateContainer,
  GameStateContainer, HandStateContainer
} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {drawCard} from '../../state/app.actions';
import {
  selectGameData,
  selectGameDocument,
  selectHandDocument,
  selectLoggedInUser
} from '../../state/app.selector';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'pkr-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.scss']
})
export class PlayComponent implements OnInit, OnDestroy {

  constructor(
    private appStore: Store<AppStateContainer>,
    private gameDataStore: Store<GameDataStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private handStore: Store<HandStateContainer>,
    private sseService: SseService,
    private toastService: ToastService) {}
  private user: UserModel;

  /**
   * Time remaining for a player to act (when applicable).
   */
  public timeToAct: number = 0;

  /**
   * Flag that informs the UI when a player is able to perform a roll action.
   */
  public canRoll: boolean = false;

  /**
   * Numbers used for the table summarizing what has occurred in the game.
   */
  public numbers: number[] = [];

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

  public ngOnDestroy() {
    this.ngDestroyed$.next();
  }

  ngOnInit(): void {
    this.appStore.select(selectLoggedInUser).subscribe(user => this.user = user);
    this.sseService.closeEvent(EmitterType.Lobby);

    // this.sseService.addCallback(EmitterType.Game, () => {
    //   if (this.gameModel.state === 'Over') {
    //     this.gameModel.hands.push('');
    //     this.canRoll = false;
    //   }
    // });

    this.sseService.openEvent(EmitterType.Hand);
    this.sseService.openEvent(EmitterType.GameData);
    this.gameDataStore.select(selectGameData)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((data: DrawGameDataModel[]) => {
        // TODO: Probably can remove this check now that GameDoc issue is resolved.
        if (data) {
          this.gameData = data;
          if (this.numbers.length === 0 && data[0] && data[0].draws) {
            this.numbers = Array(data[0].draws.length).fill('').map((v, i) => i + 1);
          }
        }
      });

    this.gameStore.select(selectGameDocument)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((gameDocument: GameDocument) => {
        this.gameModel = gameDocument;
        if (this.gameModel.state === 'Over') {
          // this.gameModel.hands.push(''); TODO: Do I need this?
          this.canRoll = false;
        }
      });

    this.handStore.select(selectHandDocument)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((handDocument: HandDocument) => {
        this.hand = handDocument;

        if (this.hand.playerToAct && this.hand.playerToAct.id) {
          this.canRoll = this.hand.playerToAct.id === this.user.id && this.gameModel.state === GameState.Play;

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
