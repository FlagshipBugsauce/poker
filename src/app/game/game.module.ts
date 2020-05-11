import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { CreateComponent } from './create/create.component';
import { ReactiveFormsModule } from '@angular/forms';
import { JoinComponent } from './join/join.component';
import { LobbyComponent } from './lobby/lobby.component';
import { GameComponent } from './game/game.component';
import { HandComponent } from './hand/hand.component';
import { PlayComponent } from './play/play.component';

@NgModule({
  declarations: [
    CreateComponent,
    JoinComponent,
    LobbyComponent,
    GameComponent,
    HandComponent,
    PlayComponent
  ],
  imports: [
    CommonModule,
    SharedModule
  ]
})
export class GameModule { }
