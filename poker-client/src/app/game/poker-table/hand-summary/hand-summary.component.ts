import {Component, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {PokerTableStateContainer} from '../../../shared/models/app-state.model';
import {
  selectDisplayHandSummary,
  selectHandWinners,
  selectPlayers
} from '../../../state/app.selector';
import {Observable, Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {GamePlayer, Winner} from '../../../api/models';
import {CardService} from '../../../shared/card.service';

@Component({
  selector: 'pkr-hand-summary',
  templateUrl: './hand-summary.component.html',
  styleUrls: ['./hand-summary.component.scss']
})
export class HandSummaryComponent implements OnInit, OnDestroy {
  /**
   * Winners of the hand. Contains all information needed by this component, including the ID's of
   * winners, the amount they won and the cards they won with.
   */
  public winners: Winner[];

  /**
   * Players in the game.
   */
  public players: GamePlayer[];

  /**
   * Observable flag used to show/hide the hand summary at the appropriate time.
   */
  public displayHandSummary$: Observable<boolean>;

  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();

  constructor(
    private pokerTableStore: Store<PokerTableStateContainer>,
    public cardService: CardService
  ) {
  }

  /**
   * Helper to avoid null pointer errors.
   */
  public get safe(): boolean {
    return this.players != null;
  }

  public ngOnInit(): void {
    this.displayHandSummary$ = this.pokerTableStore.select(selectDisplayHandSummary)
      .pipe(takeUntil(this.ngDestroyed$));
    this.pokerTableStore.select(selectHandWinners)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((winners: Winner[]) => this.winners = winners);
    this.pokerTableStore.select(selectPlayers)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((players: GamePlayer[]) => this.players = players);
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  /**
   * Returns the player model for the player with the specified ID.
   * @param id ID of the player.
   */
  public getPlayer(id: string): GamePlayer {
    return this.safe ? this.players.find(p => p.id === id) : null;
  }

  /**
   * Returns the name of the specified player with the format: '<first name> <last name>'.
   * @param player Model representing the player.
   */
  public getName(player: GamePlayer): string {
    return `${player.firstName} ${player.lastName}`;
  }

  /**
   * Returns the message that is displayed in the UI, which communicates who won, how much they won
   * and what hand they won with.
   * @param winner Model representing a winner.
   */
  public getWinnerMessage(winner: Winner): string {
    return `${this.getName(this.getPlayer(winner.id))} won $${winner.winnings} with
    ${this.cardService.getHandType(winner.type)}`;
  }
}
