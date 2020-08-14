import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  GameStateContainer,
  MiscEventsStateContainer,
  PokerTableStateContainer
} from '../../../shared/models/app-state.model';
import {Subject} from 'rxjs';
import {selectDeal, selectGamePhase, selectPlayers} from '../../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {GamePhase} from '../../../shared/models/game-phase.enum';
import {DealModel} from '../../../api/models/deal-model';
import {GamePlayerModel} from '../../../api/models/game-player-model';
import {showCard} from '../../../state/app.actions';

@Component({
  selector: 'pkr-deck',
  templateUrl: './deck.component.html',
  styleUrls: ['./deck.component.scss']
})
export class DeckComponent implements OnInit, OnDestroy {
  @Input() width: number = 200;

  public cards: number[] = Array(20).fill(0).map((v, i) => i / 2.5);

  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();
  public phase: GamePhase = GamePhase.Over;
  public showMovingCard: boolean = true;
  public movingCardStyle: { top: number; left: number; opacity: number; width: number } = {
    top: 20 / 2.5,
    left: 20 / 2.5,
    opacity: 0,
    width: this.width
  };
  public players: GamePlayerModel[] = [];
  private cardDestinations: { down: number; right: number }[] = [
    {down: 2.5, right: 4},
    {down: 2.5, right: 0.5},
    {down: 1, right: -4},
    {down: -1, right: -4},
    {down: -2.5, right: 0.5},
    {down: -2.5, right: 4},
    {down: -2.5, right: 6},
    {down: -1, right: 10},
    {down: 1, right: 10},
    {down: 2.5, right: 6}
  ];

  constructor(
    private pokerTableStore: Store<PokerTableStateContainer>,
    private gameStore: Store<GameStateContainer>,
    private miscEventStore: Store<MiscEventsStateContainer>
  ) {
  }

  ngOnInit(): void {
    this.gameStore.select(selectGamePhase)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((phase: GamePhase) => this.phase = phase);

    this.miscEventStore.select(selectDeal)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((deal: DealModel) => this.dealCards());

    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayerModel[]) => this.players = players);
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  public async dealCards(): Promise<void> {
    await this.delay(100);
    for (let i = 0; i < this.players.length; i++) {
      if (!this.players[i].out) {
        await this.sendCardToPosition(i);
      }
    }
  }

  public async sendCardToPosition(pos: number): Promise<void> {
    if (pos >= 0) {
      this.showMovingCard = true;
      let timer = 0;
      this.movingCardStyle.opacity = 1;
      while (timer++ < 30) {
        await this.delay(5);
        this.movingCardStyle.top += this.cardDestinations[pos].down * 3;
        this.movingCardStyle.left += this.cardDestinations[pos].right * 3;
        this.movingCardStyle.opacity -= 0.01;
        this.movingCardStyle.width -= 1;
      }
      this.movingCardStyle.top = 20 / 2.5;
      this.movingCardStyle.left = 20 / 2.5;
      this.movingCardStyle.opacity = 0;
      this.movingCardStyle.width = this.width;
      this.showMovingCard = false;
      this.miscEventStore.dispatch(showCard({card: pos}));
    }
  }

  private async delay(time: number): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, time));
  }
}
