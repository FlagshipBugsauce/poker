import {Component, OnInit} from '@angular/core';
import {AppStateContainer} from '../../models/app-state.model';
import {Store} from '@ngrx/store';
import {TopBarLobbyModel} from '../../models/top-bar-lobby.model';
import {
  selectCurrentGame,
  selectJwt,
  selectLastLobbyInfo,
  selectLobbyInfo
} from '../../../state/app.selector';
import {joinLobby, rejoinGame} from '../../../state/app.actions';
import {CurrentGameModel} from '../../../api/models/current-game-model';
import {Router} from '@angular/router';
import {APP_ROUTES} from '../../../app-routes';

@Component({
  selector: 'pkr-top-bar-info',
  templateUrl: './top-bar-info.component.html',
  styleUrls: ['./top-bar-info.component.scss']
})
export class TopBarInfoComponent implements OnInit {
  public lobbyInfo: TopBarLobbyModel;
  public lastLobbyInfo: TopBarLobbyModel;
  public currentGame: CurrentGameModel;
  private jwt: string;

  constructor(
    private store: Store<AppStateContainer>,
    private router: Router) {
  }

  get onGamePage(): boolean {
    return this.router.url.includes(APP_ROUTES.GAME_PREFIX.path);
  }

  ngOnInit(): void {
    // TODO: This needs work - will have back arrow when game doesn't exist + doesn't show when hosting.
    this.store.select(selectLobbyInfo).subscribe(lobbyInfo => this.lobbyInfo = lobbyInfo);
    this.store.select(selectLastLobbyInfo)
    .subscribe(lobbyInfo => this.lastLobbyInfo = lobbyInfo);
    this.store.select(selectCurrentGame)
    .subscribe((currentGame: CurrentGameModel) =>  this.currentGame = currentGame);
    this.store.select(selectJwt).subscribe(jwt => this.jwt = jwt);
  }

  public rejoinLobby(): void {
    this.store.dispatch(joinLobby(this.lastLobbyInfo));
  }

  public rejoinGame(): void {
    this.store.dispatch(rejoinGame({
      gameId: this.currentGame.id
    }));
  }
}
