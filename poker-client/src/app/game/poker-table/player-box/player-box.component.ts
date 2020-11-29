import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Card, ClientUser, GamePlayer, PokerTable} from '../../../api/models';
import {Store} from '@ngrx/store';
import {
  AppStateContainer,
  GameStateContainer,
  MiscEventsStateContainer,
  PokerTableStateContainer,
  PrivatePlayerDataStateContainer
} from '../../../shared/models/app-state.model';
import {
  selectHiddenCards,
  selectLoggedInUser,
  selectPlayers,
  selectPokerTable,
  selectPrivateCards
} from '../../../state/app.selector';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {PlayerBoxPosition} from '../../../shared/models/css-position.model';
import {PositionHelperUtil} from './position-helper.util';

@Component({
  selector: 'pkr-player-box',
  templateUrl: './player-box.component.html',
  styleUrls: ['./player-box.component.scss']
})
export class PlayerBoxComponent implements OnInit, OnDestroy {
  /*    Player positions:

            4     5     6
        3                   7
        2                   8
            1     0     9      */

  /**
   * The position on the table. This field is used to determine where the player box should be
   * positioned and which player model to use to display the correct information in the UI.
   */
  @Input() player: number;

  /**
   * Cards that all players on the table can see.
   */
  public publicCards: Card[];

  /**
   * Cards that only the player using this client can see.
   */
  public privateCards: Card[];

  /**
   * Boolean array used to help with the card dealing animation. When the first item in this array
   * is true, the first card will be hidden, when it's false, the first card will be displayed. The
   * same is true for the second item in the array w.r.t. the second card.
   */
  public hiddenCards: boolean[];

  /**
   * Model of the user who is using this client. This is used to determine if this particular player
   * box should display private cards.
   */
  public loggedInUser: ClientUser;

  /**
   * Model of the poker table.
   */
  public table: PokerTable;

  /**
   * Model of the player whose information is being displayed with this player box.
   */
  public playerModel: GamePlayer;

  /**
   * Path to the dealer button icon.
   */
  public dealerButtonPath: string = 'assets/icons/game/dealer.svg';

  /**
   * Path to the away icon.
   */
  public awayIconPath: string = 'assets/icons/afk.svg';

  /**
   * Initials of the player whose information is being displayed with this player box.
   */
  public initials: string;

  /**
   * Name of the player whose information is being displayed with this player box.
   */
  public name: string;

  /**
   * Bank roll of the player whose information is being displayed with this player box.
   */
  public bankRoll: number;

  /**
   * Away status of the player whose information is being displayed with this player box.
   */
  public away: boolean;

  /**
   * Subject which is completed when this component is destroyed. Prevents multiple subscriptions.
   */
  public ngDestroyed$: Subject<any> = new Subject<any>();

  /**
   * List of players in the game.
   */
  public players: GamePlayer[];

  constructor(
    private appStore: Store<AppStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private pokerTableStore: Store<PokerTableStateContainer>,
    private privatePlayerDataStore: Store<PrivatePlayerDataStateContainer>,
    private miscEventStore: Store<MiscEventsStateContainer>) {
  }

  /**
   * Helper to avoid null pointer errors.
   */
  public get safe(): boolean {
    return this.loggedInUser &&
      this.playerModel &&
      this.table &&
      this.players != null &&
      this.privateCards != null &&
      this.publicCards != null;
  }

  /**
   * Null safe getter for a player's current bet.
   */
  public get currentBet(): number {
    return this.safe ? this.playerModel.controls.currentBet : 0;
  }

  /**
   * Null safe getter for a player's cards.
   */
  public get cards(): Card[] {
    return !this.safe ? [] :
      this.loggedInUser.id === this.playerModel.id ? this.privateCards : this.publicCards;
  }

  /**
   * Null safe getter to determine if the dealer icon should be displayed.
   */
  public get dealer(): boolean {
    return this.safe ? this.table.dealer === this.player : false;
  }

  /**
   * Getter for the positioning information of the various elements of the player box. Positions
   * vary based on the position at the poker table.
   */
  public get positions(): PlayerBoxPosition {
    return {
      iconBox: PositionHelperUtil.getIconBoxPosition(this.player),
      awayIcon: PositionHelperUtil.getAwayIconPosition(this.player),
      chipsBox: PositionHelperUtil.getChipsBoxPosition(this.player)
    };
  }

  public ngOnInit(): void {
    this.pokerTableStore.select(selectPokerTable)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((table: PokerTable) => this.table = table);

    this.pokerTableStore.select(selectPlayers)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((players: GamePlayer[]) => {
        this.players = players;
        this.playerModel = players ? players[this.player] : null;
        if (this.playerModel) {
          this.initials =
            `${this.playerModel.firstName.substring(0, 1)}${this.playerModel.lastName.substring(0, 1)}`;
          this.name = `${this.playerModel.firstName} ${this.playerModel.lastName}`;
          this.bankRoll = this.playerModel.controls.bankRoll;
          this.publicCards = this.playerModel.cards;
          this.away = this.playerModel.away;
        }
      });

    this.appStore.select(selectLoggedInUser)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((user: ClientUser) => this.loggedInUser = user);

    this.privatePlayerDataStore.select(selectPrivateCards)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((cards: Card[]) => this.privateCards = cards);

    this.miscEventStore.select(selectHiddenCards)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((hiddenCards: boolean[][]) => this.hiddenCards = hiddenCards[this.player]);
  }

  public ngOnDestroy(): void {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }
}
