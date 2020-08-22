import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../shared/shared.module';
import {CreateComponent} from './create/create.component';
import {JoinComponent} from './join/join.component';
import {LobbyComponent} from './lobby/lobby.component';
import {GameComponent} from './game/game.component';
import {HandComponent} from './hand/hand.component';
import {EndComponent} from './end/end.component';
import {PopupAfkComponent} from './popup-afk/popup-afk.component';
import {PokerTableComponent} from './poker-table/poker-table.component';
import {PlayerBoxComponent} from './poker-table/player-box/player-box.component';
import {DeckComponent} from './poker-table/deck/deck.component';
import {HandSummaryComponent} from './poker-table/hand-summary/hand-summary.component';
import {TableControlsComponent} from './poker-table/table-controls/table-controls.component';
import {CommunityCardsComponent} from './poker-table/community-cards/community-cards.component';

@NgModule({
  declarations: [
    CreateComponent,
    JoinComponent,
    LobbyComponent,
    GameComponent,
    HandComponent,
    EndComponent,
    PopupAfkComponent,
    PokerTableComponent,
    PlayerBoxComponent,
    DeckComponent,
    HandSummaryComponent,
    TableControlsComponent,
    CommunityCardsComponent
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
