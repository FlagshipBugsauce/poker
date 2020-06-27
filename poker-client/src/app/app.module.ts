import {BrowserModule} from '@angular/platform-browser';
import {SharedModule} from './shared/shared.module';
import {NgModule} from '@angular/core';
import {AppRoutingModule} from './app-routing.module';
import {MainComponent} from './shared/main/main.component';
import {HomeModule} from './home/home.module';
import {GameModule} from './game/game.module';
import {StoreModule} from '@ngrx/store';
import {
  appReducer,
  gameDataReducer,
  gameDocumentReducer,
  gameListReducer,
  handDocumentReducer,
  lobbyDocumentReducer
} from './state/app.reducer';
import { EffectsModule } from '@ngrx/effects';
import {GameEffects} from './state/game.effects';

@NgModule({
  declarations: [],
  imports: [
    BrowserModule,
    AppRoutingModule,
    SharedModule,
    HomeModule,
    GameModule,
    StoreModule.forRoot(
      {
        appState: appReducer,
        gameData: gameDataReducer,
        lobbyDocument: lobbyDocumentReducer,
        gameDocument: gameDocumentReducer,
        handDocument: handDocumentReducer,
        gameList: gameListReducer
    }),
    EffectsModule.forRoot([GameEffects])
  ],
  exports: [
    SharedModule
  ],
  providers: [],
  bootstrap: [MainComponent]
})
export class AppModule {
}
