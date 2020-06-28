import { Component, OnInit } from '@angular/core';
import {AppStateContainer} from '../../models/app-state.model';
import {Store} from '@ngrx/store';
import {TopBarLobbyModel} from '../../models/top-bar-lobby.model';
import {selectLastLobbyInfo, selectLobbyInfo} from '../../../state/app.selector';
import {joinLobby} from '../../../state/app.actions';

@Component({
  selector: 'pkr-top-bar-info',
  templateUrl: './top-bar-info.component.html',
  styleUrls: ['./top-bar-info.component.scss']
})
export class TopBarInfoComponent implements OnInit {
  public lobbyInfo: TopBarLobbyModel;
  public lastLobbyInfo: TopBarLobbyModel;

  constructor(private store: Store<AppStateContainer>) { }

  ngOnInit(): void {
    // TODO: This needs work - will have back arrow when game doesn't exist + doesn't show when hosting.
    this.store.select(selectLobbyInfo).subscribe(lobbyInfo => this.lobbyInfo = lobbyInfo);
    this.store.select(selectLastLobbyInfo).subscribe(lobbyInfo => this.lastLobbyInfo = lobbyInfo);
  }

  public rejoin(): void {
    this.store.dispatch(joinLobby(this.lastLobbyInfo));
  }
}
