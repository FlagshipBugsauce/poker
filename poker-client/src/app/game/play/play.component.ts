import {Component, OnInit} from '@angular/core';
import {ApiConfiguration} from 'src/app/api/api-configuration';
import {Router} from '@angular/router';
import {EmittersService, GameService, HandService} from 'src/app/api/services';
import {SseService} from 'src/app/shared/sse.service';
import {ApiInterceptor} from 'src/app/api-interceptor.service';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {AuthService} from 'src/app/shared/auth.service';
import {
  CardModel, DrawGameDataModel,
  GameDocument,
  HandDocument,
} from '../../api/models';
import {ToastService} from '../../shared/toast.service';
import {GameState} from '../../shared/models/game-state.enum';

@Component({
  selector: 'pkr-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.scss']
})
export class PlayComponent implements OnInit {
  /**
   * Time remaining for a player to act (when applicable).
   */
  public timeToAct: number = 0;

  /**
   * Flag that informs the UI when a player is able to perform a roll action.
   */
  public canRoll: boolean = false;

  /**
   * Numbers used for the table summarizing what has occurred in the game.
   */
  public numbers: number[] = Array(this.gameModel.totalHands).fill('').map((v, i) => i + 1);

  /**
   * Collection of cards drawn in the current hand.
   */
  public currentHandCards: CardModel[] = [] as CardModel[];

  constructor(
    private apiConfiguration: ApiConfiguration,
    private router: Router,
    private gameService: GameService,
    private emittersService: EmittersService,
    private handService: HandService,
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor,
    private authService: AuthService,
    private toastService: ToastService) {
  }

  /**
   * Getter for the model representing the current hand.
   */
  public get hand(): HandDocument {
    return this.sseService.handDocument;
  }

  /**
   * Getter for the model representing the current game.
   */
  public get gameModel(): GameDocument {
    return this.sseService.gameDocument;
  }

  /**
   * Getter for the data representing the current game.
   */
  public get gameData(): DrawGameDataModel[] {
    return this.sseService.gameData.gameData;
  }

  ngOnInit(): void {
    this.sseService.closeEvent(EmitterType.Lobby);
    this.sseService.addCallback(EmitterType.Game, () => {
      if (this.gameModel.state === 'Over') {
        this.gameModel.hands.push('');
        this.canRoll = false;
      }
    });

    this.sseService.openEvent(EmitterType.Hand, () => {
      if (this.hand.playerToAct != null && this.hand.playerToAct.id != null) {
        this.canRoll = this.hand.playerToAct.id === this.authService.userModel.id && this.gameModel.state === GameState.Play;
        // Display toast
        const lastAction = this.hand.actions != null ? this.hand.actions[this.hand.actions.length - 1] : null;
        if (lastAction != null) {
          this.toastService.show(lastAction.message, {classname: 'bg-light toast-lg', delay: 5000});
        }
        if (this.gameModel.state !== 'Over') {
          this.startTurnTimer().then();
        }
        this.currentHandCards = this.hand.drawnCards;
      }
    });

    // TODO: Refactor this ridiculous crap.
    // let counter = -1;
    this.sseService.openEvent(EmitterType.GameData, null);
  }

  /**
   * Draws a card from the deck.
   */
  public draw(): void {
    this.handService.draw().subscribe();
  }

  /**
   * Begins a timer which will display how much time a player has to perform an action, before the action is performed for them.
   */
  private async startTurnTimer(): Promise<void> {
    const numHandActions = this.hand.actions.length;
    let currentTime = this.gameModel.timeToAct;
    while (currentTime >= 0) {
      this.timeToAct = currentTime;
      await new Promise(resolve => setTimeout(resolve, 1000));
      if (this.hand.actions.length !== numHandActions) {
        break;
      }
      this.timeToAct = --currentTime;
    }
  }
}
