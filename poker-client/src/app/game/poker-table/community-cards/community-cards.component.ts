import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {Subject} from 'rxjs';
import {Store} from '@ngrx/store';
import {PokerTableStateContainer} from '../../../shared/models/app-state.model';
import {selectCommunityCards} from '../../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {Card} from '../../../api/models/card';

@Component({
  selector: 'pkr-community-cards',
  templateUrl: './community-cards.component.html',
  styleUrls: ['./community-cards.component.scss']
})
export class CommunityCardsComponent implements OnInit, OnDestroy {
  @Input() width: number = 80;
  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$: Subject<any> = new Subject<any>();

  public cards: Card[] = [];

  constructor(private pokerTableStore: Store<PokerTableStateContainer>) {
  }

  public ngOnInit(): void {
    this.pokerTableStore.select(selectCommunityCards)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((cards: Card[]) => this.cards = cards);
  }

  public ngOnDestroy(): void {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }
}
