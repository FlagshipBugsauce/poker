import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../shared/shared.module';
import {CreateComponent} from './create/create.component';
import {JoinComponent} from './join/join.component';
import {LobbyComponent} from './lobby/lobby.component';
import {GameComponent} from './game/game.component';
import {HandComponent} from './hand/hand.component';
import {PlayComponent} from './play/play.component';
import {EndComponent} from './end/end.component';
import {PopupAfkComponent} from './popup-afk/popup-afk.component';
import {PokerTableComponent} from './poker-table/poker-table.component';
import {PlayerBoxComponent} from './poker-table/player-box/player-box.component';
import {DeckComponent} from './poker-table/deck/deck.component';

@NgModule({
  declarations: [
    CreateComponent,
    JoinComponent,
    LobbyComponent,
    GameComponent,
    HandComponent,
    PlayComponent,
    EndComponent,
    PopupAfkComponent,
    PokerTableComponent,
    PlayerBoxComponent,
    DeckComponent
  ],
  exports: [
    PokerTableComponent
  ],
  imports: [
    CommonModule,
    SharedModule
  ]
})
export class GameModule {
}
