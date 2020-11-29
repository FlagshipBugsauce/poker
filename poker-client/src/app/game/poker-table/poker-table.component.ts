import {Component, HostListener, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  AppStateContainer,
  GameStateContainer,
  MiscEventsStateContainer,
  PlayerDataStateContainer,
  PokerTableStateContainer
} from '../../shared/models/app-state.model';
import {refreshTable, setAwayStatus, showAllCards} from '../../state/app.actions';
import {Subject} from 'rxjs';
import {
  selectAwayStatus,
  selectLoggedInUser,
  selectPlayers,
  selectTimer
} from '../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {ClientUser, GamePlayer, Timer} from '../../api/models';
import {PopupAfkComponent} from '../popup-afk/popup-afk.component';

@Component({
  selector: 'pkr-poker-table',
  templateUrl: './poker-table.component.html',
  styleUrls: ['./poker-table.component.scss']
})
export class PokerTableComponent implements OnInit, OnDestroy {

  /**
   * Reference to the AFK popup, used to display/hide the modal.
   */
  @ViewChild('afkPopup') afkPopup: PopupAfkComponent;
  public width: number = 1;

  /**
   * Collection of objects used to position the player box components.
   */
  public playerBoxes: { number: number; top: number; left: number }[];

  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();

  /**
   * Players in the game.
   */
  public players: GamePlayer[] = [];

  /**
   * Object used to position the logo in the center of the poker table.
   */
  public logoPosition: { top: number; left: number; logoWidth: number; } = {
    logoWidth: 0,
    top: 0,
    left: 0
  };

  /**
   * Object used to position the deck component.
   */
  public deckPosition: { top: number; left: number; width: number } = {top: 0, left: 0, width: 200};

  /**
   * Object used to position the community cards component.
   */
  public communityCardsPosition: { top: number; left: number; width: number } =
    {top: 0, left: 0, width: 200};
  public numPlayersSet: boolean = false;
  public timerValue: number[] = [0];
  public timerIndex: number = 0;
  public minWidth: number = 1300;
  private sizeRatio: number = 5 / 3;
  private user;

  constructor(
    private appStore: Store<AppStateContainer>,
    private playerDataStore: Store<PlayerDataStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private pokerTableStore: Store<PokerTableStateContainer>,
    private miscEventsStore: Store<MiscEventsStateContainer>) {
  }

  /**
   * Height of the component, calculated dynamically based on window size.
   */
  public get height(): number {
    return this.width / this.sizeRatio;
  }

  /**
   * Width of the poker table, calculated dynamically based on window size.
   */
  public get tableWidth(): number {
    return this.width * 0.8;
  }

  /**
   * Height of the poker table, calculated dynamically based on window size.
   */
  public get tableHeight(): number {
    return this.tableWidth * 0.5;
  }

  /**
   * Border radius of the table, calculated dynamically based on window size.
   */
  public get tableBorderRadius(): number {
    return this.tableHeight * 0.5;
  }

  /**
   * Left position of the table, calculated dynamically based on window size such that the table
   * is properly centered.
   */
  public get leftTableOffset(): number {
    return this.width * 0.1;
  }

  public get topTableOffset(): number {
    return this.height * (1 / 6);
  }

  public get safe(): boolean {
    return this.user &&
      this.user.id &&
      this.players &&
      this.players.length > 0 &&
      this.players[0].id != null;
  }

  public get playersIndex(): number {
    return this.safe ? this.players.findIndex(p => p.id === this.user.id) : -1;
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
      this.width = window.innerWidth - 50;
      this.playerBoxes = Array(num)
        .fill({number: 1, top: 0, left: 0})
        .map((v, i) => ({number: i, top: 0, left: 0}));
      this.updatePositions();
    }
  }

  ngOnInit(): void {
    this.appStore.select(selectLoggedInUser)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((user: ClientUser) => this.user = user);

    this.pokerTableStore.select(selectPlayers)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((players: GamePlayer[]) => {
        this.players = players;
        if (players) {
          this.initializePlayerBoxes(players.length);
        }
      });

    this.playerDataStore.select(selectAwayStatus)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((status: boolean) => {
        if (status && this.afkPopup) {
          this.afkPopup.open();
        }
      });

    this.miscEventsStore.select(selectTimer)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((timer: Timer) => {
        this.timerIndex++;
        // tslint:disable-next-line:no-bitwise
        this.timerValue.push(timer.duration | 0);
        this.startTimer().then();
      });
  }

  @HostListener('window:resize', ['$event'])
  public onResize(event) {
    const lastWidth = this.width;
    const scrollbar: boolean = document.body.scrollHeight > document.body.clientHeight;
    const margin: number = scrollbar ? 48 : 30;
    this.width = window.innerWidth > this.minWidth ?
      window.innerWidth - margin : this.minWidth - margin;
    if (this.width !== lastWidth) {
      this.updatePositions();
    }
  }

  /**
   * Sets status to away.
   */
  public setStatusToAway(): void {
    this.playerDataStore.dispatch(setAwayStatus({away: true}));
  }

  /**
   * Manually requests a table update.
   */
  public refreshTable(): void {
    this.pokerTableStore.dispatch(refreshTable());
    this.pokerTableStore.dispatch(showAllCards());
  }

  /**
   * Begins a timer which will display how much time a player has to perform an action, before the
   * action is performed for them.
   */
  public updatePositions(): void {
    for (let i = 0; i < this.playerBoxes.length; i++) {
      this.playerBoxes[i].top = this.dimensions(i)[0];
      this.playerBoxes[i].left = this.dimensions(i)[1];
    }
    this.logoPosition.logoWidth = this.tableWidth / 6;
    this.logoPosition.top = this.tableHeight / 2.25 - this.logoPosition.logoWidth / 2;
    this.logoPosition.left = this.tableWidth / 2 - this.logoPosition.logoWidth / 2;
    this.deckPosition.width = 80;  // TODO: Revisit this.
    this.deckPosition.top = this.tableHeight / 2.55 - this.deckPosition.width / 2;
    this.deckPosition.left = this.tableWidth * 0.26;
    this.communityCardsPosition.width = this.deckPosition.width;
    this.communityCardsPosition.top = this.deckPosition.top;
    this.communityCardsPosition.left = this.deckPosition.left + this.deckPosition.width + 20;
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
