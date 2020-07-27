import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {PokerTableStateContainer} from '../../../shared/models/app-state.model';
import {selectHandSummary, selectPlayers} from '../../../state/app.selector';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {HandSummaryModel} from '../../../api/models/hand-summary-model';
import {GamePlayerModel} from '../../../api/models/game-player-model';
import {CardService} from '../../../shared/card.service';

@Component({
  selector: 'pkr-hand-summary',
  templateUrl: './hand-summary.component.html',
  styleUrls: ['./hand-summary.component.scss']
})
export class HandSummaryComponent implements OnInit, OnDestroy {
  @Input() width: number = 200;
  public summary: HandSummaryModel;
  public players: GamePlayerModel[];
  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();

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
    this.pokerTableStore.select(selectHandSummary)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((summary: HandSummaryModel) => this.summary = summary);
    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayerModel[]) => this.players = players);
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

}
