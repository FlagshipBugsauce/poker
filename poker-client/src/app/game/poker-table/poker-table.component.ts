import {Component, HostListener, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  AppStateContainer,
  GameStateContainer,
  PlayerDataStateContainer,
  PokerTableStateContainer,
  TimerStateContainer
} from '../../shared/models/app-state.model';
import {
  drawCard,
  requestGameModelUpdate,
  requestPokerTableUpdate,
  setAwayStatus,
  TimerModel
} from '../../state/app.actions';
import {Subject} from 'rxjs';
import {
  selectActingPlayer,
  selectAwayStatus,
  selectDisplayHandSummary,
  selectGameModel,
  selectLoggedInUser,
  selectPlayers,
  selectTimer
} from '../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {HandModel} from '../../api/models/hand-model';
import {GameModel} from '../../api/models/game-model';
import {GamePlayerModel} from '../../api/models/game-player-model';
import {UserModel} from '../../api/models/user-model';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';

@Component({
  selector: 'pkr-poker-table',
  templateUrl: './poker-table.component.html',
  styleUrls: ['./poker-table.component.scss']
})
export class PokerTableComponent implements OnInit, OnDestroy {

  numPlayers: number = 0;
  @ViewChild('afkPopup') afkPopup: PopupAfkComponent;
  public width: number = 1;
  public playerBoxes: { number: number; top: number; left: number }[];
  public game: GameModel;
  public hand: HandModel;
  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();
  public players: GamePlayerModel[] = [];
  public actingIndex: number;
  public displaySummary: boolean = false;
  public logoPosition: { top: number; left: number; logoWidth: number; } = {
    logoWidth: 0,
    top: 0,
    left: 0
  };
  public deckPosition: { top: number; left: number; width: number } = {top: 0, left: 0, width: 200};
  public numPlayersSet: boolean = false;
  public timerValue: number[] = [0];
  public timerIndex: number = 0;
  private sizeRatio: number = 5 / 3;
  private minWidth: number = 1300;
  private user;

  constructor(
    private appStore: Store<AppStateContainer>,
    private playerDataStore: Store<PlayerDataStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private pokerTableStore: Store<PokerTableStateContainer>,
    private timerStore: Store<TimerStateContainer>) {
  }

  public get height(): number {
    return this.width / this.sizeRatio;
  }

  public get tableWidth(): number {
    return this.width * 0.8;
  }

  public get tableHeight(): number {
    return this.tableWidth * 0.5;
  }

  public get tableBorderRadius(): number {
    return this.tableHeight * 0.5;
  }

  public get leftTableOffset(): number {
    return this.width * 0.1;
  }

  public get topTableOffset(): number {
    return this.height * (1 / 6);
  }

  public get safeToCheck(): boolean {
    return this.user && this.user.id && this.players && this.players.length > 0 && this.players[0].id != null;
  }

  public get acting(): boolean {
    return this.safeToCheck ? this.players[this.actingIndex].id === this.user.id && !this.displaySummary : false;
  }

  public get playersIndex(): number {
    return this.safeToCheck ? this.players.findIndex(p => p.id === this.user.id) : -1;
  }

  public get away(): boolean {
    return this.playersIndex !== -1 ? this.players[this.playersIndex].away : false;
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  public initializePlayerBoxes(num: number): void {
    if (!this.numPlayersSet && num > 0) {
      this.numPlayersSet = true;
      this.numPlayers = num;
      this.delay(100).then(() => {
        this.width = window.innerWidth - 50;
        this.playerBoxes = Array(this.numPlayers)
        .fill({number: 1, top: 0, left: 0})
        .map((v, i) => ({number: i + 1, top: 0, left: 0}));
        this.updatePositions();
        this.pokerTableStore.dispatch(requestPokerTableUpdate());
      });
    }
  }

  ngOnInit(): void {
    this.appStore.select(selectLoggedInUser)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((user: UserModel) => this.user = user);

    this.delay(100).then(() => this.gameStore.dispatch(requestGameModelUpdate()));
    this.gameStore.select(selectGameModel)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((game: GameModel) => this.game = game);

    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayerModel[]) => {
      this.players = players;
      if (players) {
        this.initializePlayerBoxes(players.length);
      }
    });

    this.pokerTableStore.select(selectActingPlayer)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((actingIndex: number) => this.actingIndex = actingIndex);

    this.pokerTableStore.select(selectDisplayHandSummary)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((displaySummary: boolean) => this.displaySummary = displaySummary);

    this.playerDataStore.select(selectAwayStatus)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((status: boolean) => {
      if (status && this.afkPopup) {
        this.afkPopup.open();
      }
    });

    this.timerStore.select(selectTimer)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((timer: TimerModel) => {
      this.timerIndex++;
      // tslint:disable-next-line:no-bitwise
      this.timerValue.push(timer.duration | 0);
      this.startTimer().then();
    });

    this.pokerTableStore.dispatch(requestPokerTableUpdate());
  }

  @HostListener('window:resize', ['$event'])
  onResize(event) {
    const lastWidth = this.width;
    const scrollbar: boolean = document.body.scrollHeight > document.body.clientHeight;
    const margin: number = scrollbar ? 48 : 30;
    this.width = window.innerWidth > this.minWidth ? window.innerWidth - margin : this.minWidth - margin;
    if (this.width !== lastWidth) {
      this.updatePositions();
    }
  }

  /**
   * Draws a card.
   */
  public draw(): void {
    this.appStore.dispatch(drawCard());
  }

  /**
   * Sets status to away.
   */
  public setStatusToAway(): void {
    this.playerDataStore.dispatch(setAwayStatus({away: true}));
  }

  /**
   * Starts the timer on the poker table.
   */
  private async startTimer(): Promise<void> {
    const timerIndex = this.timerIndex;
    while (this.timerValue[timerIndex] > 0) {
      await this.delay(1000);
      this.timerValue[timerIndex]--;
    }
    this.timerValue[timerIndex] = 0;
  }

  /**
   * Begins a timer which will display how much time a player has to perform an action, before the
   * action is performed for them.
   */

  private updatePositions(): void {
    for (let i = 0; i < this.numPlayers; i++) {
      this.playerBoxes[i].top = this.dimensions(i)[0];
      this.playerBoxes[i].left = this.dimensions(i)[1];
    }
    this.logoPosition.logoWidth = this.tableWidth / 6;
    this.logoPosition.top = this.tableHeight / 2.25 - this.logoPosition.logoWidth / 2;
    this.logoPosition.left = this.tableWidth / 2 - this.logoPosition.logoWidth / 2;
    this.deckPosition.width = this.tableWidth / 10;
    this.deckPosition.top = this.tableHeight / 2.55 - this.deckPosition.width / 2;
    this.deckPosition.left = this.tableWidth * 0.2;
  }

  /**
   * Helper that retrieves positioning information for a given window size. Everything is based off
   * of the width of the table container element, which is based off of the width of the window.
   * @param index Index of the playerBoxes array.
   */
  private dimensions(index: number): number[] {
    switch (index) {
      case 0:
        return [this.tableHeight - 140, this.tableWidth / 2 - 125];
      case 1:
        return [
          this.tableHeight - 140,
          Math.min(this.tableWidth / 4 - 125, this.tableWidth / 2 - 400)
        ];
      case 2:
        return [this.tableHeight * 0.75 - 170, -125];
      case 3:
        return [this.tableHeight * 0.25 - 80, -125];
      case 4:
        return [-118, Math.min(this.tableWidth / 4 - 125, this.tableWidth / 2 - 400)];
      case 5:
        return [-118, this.tableWidth / 2 - 125];
      case 6:
        return [-118, Math.max(3 * this.tableWidth / 4 - 125, this.tableWidth / 2 + 150)];
      case 7:
        return [this.tableHeight * 0.25 - 80, this.tableWidth - 165];
      case 8:
        return [this.tableHeight * 0.75 - 170, this.tableWidth - 165];
      case 9:
        return [
          this.tableHeight - 140,
          Math.max(3 * this.tableWidth / 4 - 125, this.tableWidth / 2 + 150)
        ];
      default:
        return [0, 0];
    }
  }

  /**
   * Helper which allows for running a callback after the specified delay. Useful since Angular
   * property binding results in annoying console errors if a property changes too soon after a
   * component loads. By having a small delay, we can get rid of this annoying console error.
   * Use delay(...delay).then(callback function);
   * @param time Duration of the delay.
   */
  private async delay(time: number): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, time));
  }
}
