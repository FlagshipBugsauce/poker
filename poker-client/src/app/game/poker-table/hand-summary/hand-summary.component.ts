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
import {GamePlayerModel, HandSummaryModel, WinnerModel} from '../../../api/models';
import {CardService} from '../../../shared/card.service';

@Component({
  selector: 'pkr-hand-summary',
  templateUrl: './hand-summary.component.html',
  styleUrls: ['./hand-summary.component.scss']
})
export class HandSummaryComponent implements OnInit, OnDestroy {
  @Input() width: number = 200;
  public summary: HandSummaryModel;
  public winners: WinnerModel[];
  public players: GamePlayerModel[];
  public displayHandSummary$: Observable<boolean>;
  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();

  public get safe(): boolean {
    return this.players != null;
  }

  constructor(
    private pokerTableStore: Store<PokerTableStateContainer>,
    public cardService: CardService
  ) {
  }

  public get name(): string {
    let winner: GamePlayerModel;
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
    .subscribe((summary: HandSummaryModel) => this.summary = summary);
    this.pokerTableStore.select(selectHandWinners)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((winners: WinnerModel[]) => this.winners = winners);
    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayerModel[]) => this.players = players);
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  public getPlayer(id: string): GamePlayerModel {
    return this.safe ? this.players.find(p => p.id === id) : null;
  }

  public getName(player: GamePlayerModel): string {
    return `${player.firstName} ${player.lastName}`;
  }

  public getWinnerMessage(winner: WinnerModel): string {
    return `${this.getName(this.getPlayer(winner.id))} won $${winner.winnings}`;
  }
}
