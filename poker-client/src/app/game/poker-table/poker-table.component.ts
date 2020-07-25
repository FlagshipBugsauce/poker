/* tslint:disable */
import {AfterViewInit, Component, HostListener, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from "@ngrx/store";
import {
  AppStateContainer,
  GameStateContainer,
  PlayerDataStateContainer,
  PokerTableStateContainer
} from "../../shared/models/app-state.model";
import {
  drawCard,
  requestGameModelUpdate,
  requestPokerTableUpdate,
  setAwayStatus
} from "../../state/app.actions";
import {Observable, Subject} from "rxjs";
import {
  selectActingPlayer,
  selectActingStatus,
  selectAwayStatus,
  selectDisplayHandSummary,
  selectGameModel,
  selectPlayers
} from "../../state/app.selector";
import {takeUntil} from "rxjs/operators";
import {HandModel} from "../../api/models/hand-model";
import {GameModel} from "../../api/models/game-model";
import {GamePlayerModel} from "../../api/models/game-player-model";

@Component({
  selector: 'pkr-poker-table',
  templateUrl: './poker-table.component.html',
  styleUrls: ['./poker-table.component.scss']
})
export class PokerTableComponent implements OnInit, AfterViewInit, OnDestroy {
  @Input() numPlayers: number;
  public width: number = 1;
  public playerBoxes: { number: number; top: number; left: number }[];
  public away$: Observable<boolean>;
  public game: GameModel;
  public hand: HandModel;
  public timeToAct: number;
  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();
  public acting$: Observable<boolean>;
  public players: GamePlayerModel[] = [];
  public acting: number;
  public displaySummary: boolean = false;
  public logoPosition: { top: number; left: number; logoWidth: number; } = {
    logoWidth: 0,
    top: 0,
    left: 0
  };
  public deckPosition: { top: number; left: number; width: number } = {top: 0, left: 0, width: 200};
  private sizeRatio: number = 5 / 3;
  private minWidth: number = 1300;

  constructor(
    private appStore: Store<AppStateContainer>,
    private playerDataStore: Store<PlayerDataStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private pokerTableStore: Store<PokerTableStateContainer>) {
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

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  ngOnInit(): void {
    this.acting$ = this.playerDataStore.select(selectActingStatus).pipe(takeUntil(this.ngDestroyed$));
    this.away$ = this.playerDataStore.select(selectAwayStatus).pipe(takeUntil(this.ngDestroyed$));

    this.delay(100).then(() => this.gameStore.dispatch(requestGameModelUpdate()));
    this.gameStore.select(selectGameModel)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((game: GameModel) => this.game = game);

    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayerModel[]) => this.players = players);

    this.pokerTableStore.select(selectActingPlayer)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((acting: number) => {
      this.acting = acting;
      this.startTurnTimer().then();
    });

    this.pokerTableStore.select(selectDisplayHandSummary)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((displaySummary: boolean) => this.displaySummary = displaySummary);
  }

  ngAfterViewInit(): void {
    this.delay(100).then(() => {
      this.width = window.innerWidth - 50;
      this.playerBoxes = Array(this.numPlayers)
      .fill({number: 1, top: 0, left: 0})
      .map((v, i) => ({number: i + 1, top: 0, left: 0}));
      this.updatePositions();
      this.pokerTableStore.dispatch(requestPokerTableUpdate());
    });
  }

  @HostListener('window:resize', ['$event'])
  onResize(event) {
    const lastWidth = this.width;
    this.width = window.innerWidth > this.minWidth ? window.innerWidth - 50 : this.minWidth - 50;
    if (this.width !== lastWidth) {
      this.updatePositions();
    }
  }

  public draw(): void {
    this.appStore.dispatch(drawCard());
  }

  public away(): void {
    this.playerDataStore.dispatch(setAwayStatus({away: true}));
  }

  /**
   * Begins a timer which will display how much time a player has to perform an action, before the
   * action is performed for them.
   */
  private async startTurnTimer(): Promise<void> {
    if (this.players && this.players.length > 0) {
      const acting = this.acting;
      let currentTime = this.players[this.acting].away ? 0 : this.game.timeToAct;
      while (currentTime > 0 && this.acting === acting && !this.displaySummary) {
        this.timeToAct = currentTime--;
        await this.delay(1000);
      }
      this.timeToAct = 0;
    }
  }

  private updatePositions(): void {
    // TODO: Have player # and also table position #
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
