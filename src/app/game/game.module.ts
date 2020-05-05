import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../shared/shared.module';
import { CreateComponent } from './create/create.component';
import { ReactiveFormsModule } from '@angular/forms';
import { JoinComponent } from './join/join.component';
import { LobbyComponent } from './lobby/lobby.component';
import { GameComponent } from './game/game.component';

@NgModule({
  declarations: [
    CreateComponent,
    JoinComponent,
    LobbyComponent,
    GameComponent
  ],
  imports: [
    CommonModule,
    SharedModule
  ]
})
export class GameModule { }
