import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ToastService} from 'src/app/shared/toast.service';
import {GameService} from 'src/app/api/services';
import {ApiSuccessModel, CreateGameModel, UserModel} from 'src/app/api/models';
import {AppStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {joinLobby} from '../../state/app.actions';
import {selectLoggedInUser} from '../../state/app.selector';
import {take, takeLast} from 'rxjs/operators';

@Component({
  selector: 'pkr-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss']
})
export class CreateComponent implements OnInit {
  public createGameForm: FormGroup;
  public maxPlayers: number = 10; // TODO: Retrieve this from the backend.

  constructor(
    private router: Router,
    private formBuilder: FormBuilder,
    public toastService: ToastService,
    private gameService: GameService,
    private store: Store<AppStateContainer>) {
  }

  public ngOnInit(): void {
    this.createGameForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      maxPlayers: [2, [Validators.required]],
      buyIn: ['', [Validators.required]],
      // roundTime: ['', [Validators.required]],
    });
  }

  /**
   *
   */
  public fillNumbers(): number[] {
    return Array(this.maxPlayers - 1).fill(this.maxPlayers).map((x, i) => i + 2);
  }

  public createGame(values: any): void {
    this.gameService.createGame({
      body: {
        buyIn: values.buyIn,
        maxPlayers: values.maxPlayers,
        name: values.name
      } as CreateGameModel
    }).subscribe((response: ApiSuccessModel) => {
      this.router.navigate([`/game/${response.message}`]).then();
      // Select loggedInUser from store and then dispatch the joinLobby action.
      this.store.select(selectLoggedInUser).pipe(take(1)).subscribe(val => {
        this.store.dispatch(joinLobby({
          id: response.message,
          name: values.name,
          host: ({...val})
        }));
      });
    });
  }
}
