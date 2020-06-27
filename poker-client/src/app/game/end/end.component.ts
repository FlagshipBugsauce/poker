import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {DrawGameDataModel, GameDocument} from 'src/app/api/models';
import {GameStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {selectGameDocument} from '../../state/app.selector';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'pkr-end',
  templateUrl: './end.component.html',
  styleUrls: ['./end.component.scss']
})
export class EndComponent implements OnInit, OnDestroy {
  /**
   * Game data that is used to display a summary of what occurred in the game.
   */
  @Input() gameData: DrawGameDataModel[] = [] as DrawGameDataModel[];

  /**
   * Array of numbers used for the game summary.
   */
  public numbers: number[] = [];

  constructor(private gameStore: Store<GameStateContainer>) {}

  /**
   * Getter for the game model.
   */
  public gameModel: GameDocument;

  public ngDestroyed$ = new Subject();

  public ngOnDestroy() {
    this.ngDestroyed$.next();
  }

  ngOnInit(): void {
    this.gameStore.select(selectGameDocument)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((gameDocument: GameDocument) => {
        this.gameModel = gameDocument;
        this.numbers = Array(gameDocument.totalHands).fill('').map((v, i) => i + 1);
      });
  }
}
