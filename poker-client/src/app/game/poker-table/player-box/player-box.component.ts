/* tslint:disable:no-bitwise */
import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Card, GamePlayer, PokerTable, User} from '../../../api/models';
import {CardSuit, CardValue} from '../../../shared/models/card.enum';
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
import {PlayerBoxPositionModel} from '../../../shared/models/css-position.model';
import {PositionHelperUtil} from './position-helper.util';

// TODO: Add some kind of async process that will wait until it receives all needed info.

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

  @Input() player: number;
  public publicCards: Card[] = [
    {value: CardValue.Ace, suit: CardSuit.Spades},
    {value: CardValue.Ace, suit: CardSuit.Spades}
  ];
  public privateCards: Card[] = [
    {value: CardValue.Ace, suit: CardSuit.Spades},
    {value: CardValue.Ace, suit: CardSuit.Spades}
  ];

  public hideCards: boolean = false;
  public hiddenCards: boolean[] = [false, false];
  public loggedInUser: User;
  public table: PokerTable;
  public playerModel: GamePlayer = {} as GamePlayer;
  public dealerButtonPath: string = 'assets/icons/game/dealer.svg';
  public awayIconPath: string = 'assets/icons/afk.svg';
  public initials: string = 'AB';
  public name: string = 'Jackson McGillicutty';
  public bankRoll: number = 420.69;
  public away: boolean = false;
  public ngDestroyed$: Subject<any> = new Subject<any>();
  public players: GamePlayer[];

  constructor(
    private appStore: Store<AppStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private pokerTableStore: Store<PokerTableStateContainer>,
    private privatePlayerDataStore: Store<PrivatePlayerDataStateContainer>,
    private miscEventStore: Store<MiscEventsStateContainer>) {
  }

  public get safe(): boolean {
    return this.loggedInUser &&
      this.playerModel &&
      this.table &&
      this.players != null &&
      this.privateCards != null &&
      this.publicCards != null;
  }

  public get currentBet(): number {
    return this.safe ? this.playerModel.controls.currentBet : 0;
  }

  public get cards(): Card[] {
    return !this.safe ? [{value: CardValue.Ace, suit: CardSuit.Spades}] :
      this.loggedInUser.id === this.playerModel.id ? this.privateCards : this.publicCards;
  }

  public get dealer(): boolean {
    return this.safe ? this.table.dealer === this.player : false;
  }

  public get positions(): PlayerBoxPositionModel {
    return {
      iconBox: PositionHelperUtil.getIconBoxPosition(this.player),
      awayIcon: PositionHelperUtil.getAwayIconPosition(this.player),
      chipsBox: PositionHelperUtil.getChipsBoxPosition(this.player)
    };
  }

  ngOnInit(): void {
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
    .subscribe((user: User) => this.loggedInUser = user);

    this.privatePlayerDataStore.select(selectPrivateCards)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((cards: Card[]) => this.privateCards = cards);

    // this.miscEventStore.select(selectHiddenCards)
    // .pipe(takeUntil(this.ngDestroyed$))
    // .subscribe((hiddenCards: boolean[]) => this.hideCards = hiddenCards[this.player]);
    this.miscEventStore.select(selectHiddenCards)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((hiddenCards: boolean[][]) => this.hiddenCards = hiddenCards[this.player]);
  }

  ngOnDestroy(): void {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }
}
