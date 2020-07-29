/* tslint:disable:no-bitwise */
import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {CardModel, GamePlayerModel} from '../../../api/models';
import {CardSuit, CardValue} from '../../../shared/models/card.enum';
import {Store} from '@ngrx/store';
import {GameStateContainer, PokerTableStateContainer} from '../../../shared/models/app-state.model';
import {selectPlayers} from '../../../state/app.selector';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {CardComponent} from '../../../shared/card/card.component';

@Component({
  selector: 'pkr-player-box',
  templateUrl: './player-box.component.html',
  styleUrls: ['./player-box.component.scss']
})
export class PlayerBoxComponent implements OnInit, OnDestroy {
  /*    Player positions:

            5     6     7
        4                   8
        3                   9
            2     1     10      */

  @Input() player: number;
  @ViewChild('card1') card1: CardComponent;
  public cards: CardModel[] = [{value: CardValue.Ace, suit: CardSuit.Spades}];

  public playerModel: GamePlayerModel = {} as GamePlayerModel;

  public dealerButtonPath: string = 'assets/icons/game/dealer.svg';
  public awayIconPath: string = 'assets/icons/afk.svg';
  public initials: string = 'AB';
  public name: string = 'Jackson McGillicutty';
  public bankRoll: number = 420.69;
  public score: number = 420;
  public away: boolean = false;
  public ngDestroyed$: Subject<any> = new Subject<any>();
  public players: GamePlayerModel[];
  private dealerInternal: boolean = false;

  constructor(
    private gameStore: Store<GameStateContainer>,
    private pokerTableStore: Store<PokerTableStateContainer>) {
  }

  public get dealer(): boolean {
    return this.playerModel ? this.dealerInternal : false;
  }

  public get iconBoxPosition(): { top: number; left: number } {
    const position: { top: number; left: number } = {top: 0, left: 0};
    switch (this.player) {
      case 1:
      case 2:
      case 10:
        position.top = -40;
        break;
      case 3:
      case 4:
      case 8:
      case 9:
        position.top = 60;
        break;
      case 5:
      case 6:
      case 7:
        position.top = 165;
        break;
    }
    switch (this.player) {
      case 1:
      case 6:
        position.left = 110;
        break;
      case 2:
      case 5:
        position.left = 185;
        break;
      case 3:
      case 4:
        position.left = 265;
        break;
      case 7:
      case 10:
        position.left = 35;
        break;
      case 8:
      case 9:
        position.left = -45;
        break;
    }
    return position;
  }

  public get awayIconPosition(): { top: number; left: number } {
    const position: { top: number; left: number } = {top: 0, left: 0};
    switch (this.player) {
      case 1:
      case 6:
        position.left = 33;
        break;
      case 2:
      case 5:
        position.left = -33;
        break;
      case 3:
      case 4:
        position.top = 33;
        break;
      case 7:
      case 10:
        position.left = 33;
        break;
      case 8:
      case 9:
        position.top = 33;
        break;
    }
    return position;
  }

  ngOnInit(): void {
    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayerModel[]) => {
      this.players = players;
      this.playerModel = players ? players[this.player - 1] : null;
      if (this.playerModel) {
        this.initials =
          `${this.playerModel.firstName.substring(0, 1)}${this.playerModel.lastName.substring(0, 1)}`;
        this.name = `${this.playerModel.firstName} ${this.playerModel.lastName}`;
        this.bankRoll = this.playerModel.bankRoll;
        this.dealerInternal = this.playerModel.acting;
        this.cards = this.playerModel.cards;
        this.score = this.playerModel.score;
        this.away = this.playerModel.away;
      }
    });
  }

  ngOnDestroy(): void {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }
}
