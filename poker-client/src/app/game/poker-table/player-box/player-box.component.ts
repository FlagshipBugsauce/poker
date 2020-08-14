/* tslint:disable:no-bitwise */
import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {CardModel, GamePlayerModel, PokerTableModel, UserModel} from '../../../api/models';
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
import {CardComponent} from '../../../shared/card/card.component';
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
  @ViewChild('card1') card1: CardComponent;
  public publicCards: CardModel[] = [{value: CardValue.Ace, suit: CardSuit.Spades}];
  public privateCards: CardModel[] = [{value: CardValue.Ace, suit: CardSuit.Spades}];

  public hideCards: boolean = false;
  public loggedInUser: UserModel;
  public table: PokerTableModel;
  public playerModel: GamePlayerModel = {} as GamePlayerModel;
  public dealerButtonPath: string = 'assets/icons/game/dealer.svg';
  public awayIconPath: string = 'assets/icons/afk.svg';
  public initials: string = 'AB';
  public name: string = 'Jackson McGillicutty';
  public bankRoll: number = 420.69;
  public away: boolean = false;
  public ngDestroyed$: Subject<any> = new Subject<any>();
  public players: GamePlayerModel[];

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

  public get cards(): CardModel[] {
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
    .subscribe((table: PokerTableModel) => this.table = table);

    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayerModel[]) => {
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
    .subscribe((user: UserModel) => this.loggedInUser = user);

    this.privatePlayerDataStore.select(selectPrivateCards)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((cards: CardModel[]) => this.privateCards = cards);

    this.miscEventStore.select(selectHiddenCards)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((hiddenCards: boolean[]) => this.hideCards = hiddenCards[this.player]);
  }

  ngOnDestroy(): void {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }
}
