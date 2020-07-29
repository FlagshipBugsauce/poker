import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {DrawGameDataModel, GameModel} from 'src/app/api/models';
import {GameStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {selectGameModel} from '../../state/app.selector';
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
  /**
   * Getter for the game model.
   */
  public gameModel: GameModel;
  public ngDestroyed$ = new Subject();

  constructor(private gameStore: Store<GameStateContainer>) {
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
  }

  ngOnInit(): void {
    this.gameStore.select(selectGameModel)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((gameModel: GameModel) => {
      this.gameModel = gameModel;
      this.numbers = Array(gameModel.totalHands).fill('').map((v, i) => i + 1);
    });
    console.log(this.gameData);
  }
}
