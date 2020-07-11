import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AppStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {createGame} from '../../state/app.actions';
import {CreateGameService} from '../../shared/web-socket/create-game.service';
import {GameParameterModel} from '../../api/models/game-parameter-model';

@Component({
  selector: 'pkr-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss']
})
export class CreateComponent implements OnInit, OnDestroy {
  public createGameForm: FormGroup;
  public maxPlayers: number = 10; // TODO: Retrieve this from the backend.

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    private appStore: Store<AppStateContainer>,
    private createGameService: CreateGameService) {
  }

  public ngOnInit(): void {
    this.createGameForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      maxPlayers: [2, [Validators.required]],
      buyIn: ['', [Validators.required]],
      // roundTime: ['', [Validators.required]],
    });

    this.createGameService.subscribeToCreateGameTopic();
  }

  public ngOnDestroy(): void {
    this.createGameService.unsubscribeFromCreateGameTopic();
  }

  /**
   * Fills the numbers in the num players select.
   */
  public fillNumbers(): number[] {
    return Array(this.maxPlayers - 1).fill(this.maxPlayers).map((x, i) => i + 2);
  }

  public createGame(values: any): void {
    this.appStore.dispatch(createGame({
      buyIn: values.buyIn,
      maxPlayers: values.maxPlayers,
      name: values.name
    } as GameParameterModel));
  }
}
