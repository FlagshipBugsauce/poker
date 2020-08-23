import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  GameStateContainer,
  MiscEventsStateContainer,
  PokerTableStateContainer
} from '../../../shared/models/app-state.model';
import {Subject} from 'rxjs';
import {
  selectDeal,
  selectDealer,
  selectGamePhase,
  selectPlayers
} from '../../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {GamePhase} from '../../../shared/models/game-phase.enum';
import {Deal} from '../../../api/models/deal';
import {GamePlayer} from '../../../api/models/game-player';
import {showCard} from '../../../state/app.actions';

@Component({
  selector: 'pkr-deck',
  templateUrl: './deck.component.html',
  styleUrls: ['./deck.component.scss']
})
export class DeckComponent implements OnInit, OnDestroy {
  /**
   * The width of the cards in the deck. This value comes from the poker table component, which
   * calculates the width based on the size of the poker table.
   * TODO: Currently the value is actually a constant value of 80 - need to investigate whether
   *  this should be constant or variable based on table size.
   */
  @Input() width: number = 200;

  /**
   * The deck is given a 3-D appearance by stacking ~20 or so cards on top of each other and
   * off-setting their positions slightly. This field is an array of the offsets, which is used
   * to position the cards that make up the deck.
   */
  public cards: number[] = Array(20).fill(0).map((v, i) => i / 2.5);

  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();

  /**
   * The current phase of the game. TODO: Investigate whether this is actually needed.
   */
  public phase: GamePhase = GamePhase.Over;

  /**
   * Animated cards will only be visible when this is set to true. TODO: Investigate if necessary.
   */
  public showMovingCard: boolean = true;

  /**
   * Object used to style (via NgStyle directive) moving cards.
   */
  public movingCardStyle: { top: number; left: number; opacity: number; width: number } = {
    top: 20 / 2.5,
    left: 20 / 2.5,
    opacity: 0,
    width: this.width
  };

  /**
   * List of players sitting at the table.
   */
  public players: GamePlayer[] = [];

  /**
   * The position of the dealer at the table. Card dealing animations need to begin at the first
   * active player clockwise (or to the left) of the dealer.
   */
  public dealer: number = 0;

  /**
   * Object used to determine the amount of pixels a card should move in each frame of the dealing
   * animation.
   * TODO: Investigate a better solution - hard-coding these values is not ideal.
   */
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
    .subscribe((deal: Deal) => this.dealCards());

    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayer[]) => this.players = players);

    this.pokerTableStore.select(selectDealer)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((dealer: number) => this.dealer = dealer);
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  /**
   * Controller for the card dealing animation. Will begin dealing at the player immediately
   * clockwise of the dealer. The client knows there are cards, but is hiding them until an action
   * which reveals the cards is dispatched.
   */
  public async dealCards(): Promise<void> {
    await this.delay(100);
    const n = this.players.length;
    for (let i = 1; i <= 2 * n; i++) {
      if (!this.players[(this.dealer + i) % n].out) {
        await this.sendCardToPosition((this.dealer + i) % n, i > n ? 1 : 0);
      }
    }
  }

  /**
   * Triggers an animation which sends a card from the deck to the specified position. Once the card
   * reaches that position, an action is dispatched which will un-hide the card in the player box
   * at the specified position.
   *
   * @param pos The position the card is being sent to.
   * @param card The card being dealt (can be either 0 or 1).
   */
  public async sendCardToPosition(pos: number, card: number): Promise<void> {
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
      this.miscEventStore.dispatch(showCard({player: pos, card}));
    }
  }

  /**
   * Helper function which will cause a delay. Used to assist with dealing animations.
   *
   * @param time The duration of the delay.
   */
  private async delay(time: number): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, time));
  }
}
