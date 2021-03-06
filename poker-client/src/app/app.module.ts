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
  chatReducer,
  gameDataReducer,
  gameListReducer,
  gameModelReducer,
  lobbyModelReducer,
  miscEventsReducer,
  playerDataReducer,
  pokerTableReducer,
  privatePlayerDataReducer,
  toastDataReducer
} from './state/app.reducer';
import {EffectsModule} from '@ngrx/effects';
import {GameEffects} from './state/game.effects';
import {AppEffects} from './state/app.effects';
import {ChatEffects} from './state/chat.effects';

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
        lobbyModel: lobbyModelReducer,
        gameModel: gameModelReducer,
        gameList: gameListReducer,
        playerData: playerDataReducer,
        lastToast: toastDataReducer,
        chats: chatReducer,
        tableState: pokerTableReducer,
        miscEvents: miscEventsReducer,
        privatePlayerData: privatePlayerDataReducer
      }),
    EffectsModule.forRoot([AppEffects, GameEffects, ChatEffects])
  ],
  exports: [
    SharedModule
  ],
  providers: [],
  bootstrap: [MainComponent]
})
export class AppModule {
}
