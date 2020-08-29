import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {PokerTableStateContainer} from '../../../shared/models/app-state.model';
import {
  selectDisplayHandSummary,
  selectHandSummary,
  selectHandWinners,
  selectPlayers
} from '../../../state/app.selector';
import {Observable, Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {GamePlayer, HandSummary, Winner} from '../../../api/models';
import {CardService} from '../../../shared/card.service';

@Component({
  selector: 'pkr-hand-summary',
  templateUrl: './hand-summary.component.html',
  styleUrls: ['./hand-summary.component.scss']
})
export class HandSummaryComponent implements OnInit, OnDestroy {
  @Input() width: number = 200;
  public summary: HandSummary;
  public winners: Winner[];
  public players: GamePlayer[];
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

  public get safe(): boolean {
    return this.players != null;
  }

  public get name(): string {
    let winner: GamePlayer;
    if (this.players && this.summary) {
      winner = this.players[this.summary.winner];
    } else {
      return '';
    }
    return `${winner.firstName} ${winner.lastName}`;
  }

  ngOnInit(): void {
    this.displayHandSummary$ = this.pokerTableStore.select(selectDisplayHandSummary)
    .pipe(takeUntil(this.ngDestroyed$));
    this.pokerTableStore.select(selectHandSummary)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((summary: HandSummary) => this.summary = summary);
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

  public getPlayer(id: string): GamePlayer {
    return this.safe ? this.players.find(p => p.id === id) : null;
  }

  public getName(player: GamePlayer): string {
    return `${player.firstName} ${player.lastName}`;
  }

  public getWinnerMessage(winner: Winner): string {
    return `${this.getName(this.getPlayer(winner.id))} won $${winner.winnings} with
    ${this.cardService.getHandType(winner.type)}`;
  }
}
