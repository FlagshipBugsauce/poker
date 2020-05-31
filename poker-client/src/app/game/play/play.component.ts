import {Component, OnInit} from '@angular/core';
import {ApiConfiguration} from 'src/app/api/api-configuration';
import {Router} from '@angular/router';
import {EmittersService, GameService, HandService} from 'src/app/api/services';
import {SseService} from 'src/app/shared/sse.service';
import {ApiInterceptor} from 'src/app/api-interceptor.service';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {AuthService} from 'src/app/shared/auth.service';
import {
  CardModel,
  GameDocument,
  GamePlayerModel,
  HandDocument,
  PlayerModel
} from '../../api/models';
import {ToastService} from '../../shared/toast.service';
import {GameState} from '../../shared/models/game-state.enum';
import {CardSuit, CardValue} from "../../shared/models/card.enum";

@Component({
  selector: 'pkr-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.scss']
})
export class PlayComponent implements OnInit {
  /**
   * Data representing a summary of the game.
   */
  public gameData: PlayerStatModel[] = [];

  /**
   * Current hand being played in the game.
   */
  public currentHand: number = 0;
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
  public numbers: number[];

  public suitMapping = {};

  public valueMapping = {};

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
    this.suitMapping[CardSuit.Spades] = 'S';
    this.suitMapping[CardSuit.Hearts] = 'H';
    this.suitMapping[CardSuit.Clubs] = 'C';
    this.suitMapping[CardSuit.Diamonds] = 'D';
    this.valueMapping[CardValue.Ace] = 'A';
    this.valueMapping[CardValue.King] = 'K';
    this.valueMapping[CardValue.Queen] = 'Q';
    this.valueMapping[CardValue.Jack] = 'J';
    this.valueMapping[CardValue.Ten] = '10';
    this.valueMapping[CardValue.Nine] = '9';
    this.valueMapping[CardValue.Eight] = '8';
    this.valueMapping[CardValue.Seven] = '7';
    this.valueMapping[CardValue.Six] = '6';
    this.valueMapping[CardValue.Five] = '5';
    this.valueMapping[CardValue.Four] = '4';
    this.valueMapping[CardValue.Three] = '3';
    this.valueMapping[CardValue.Two] = '2';
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

  ngOnInit(): void {
    this.sseService.closeEvent(EmitterType.Lobby);
    this.sseService.addCallback(EmitterType.Game, () => {
      if (this.gameModel.state === 'Over') {
        this.gameModel.hands.push('');
        this.canRoll = false;
      }
      this.updateScore();
      this.highlightBestScore();
    });

    this.sseService.openEvent(EmitterType.Hand, () => {
      if (this.hand.playerToAct != null && this.hand.playerToAct.id != null) {
        this.canRoll = this.hand.playerToAct.id === this.authService.userModel.id && this.gameModel.state === GameState.Play;
        // Add the message to
        const lastAction = this.hand.actions != null ? this.hand.actions[this.hand.actions.length - 1] : null;
        if (lastAction != null) {
          this.toastService.show(lastAction.message, {classname: 'bg-light toast-lg', delay: 5000});
        }
        this.updateGameData();
        if (this.gameModel.state !== 'Over') {
          this.highlightWaitingToAct();
          this.startTurnTimer().then();
        }
      }
    });
    this.initializeGameData();
  }

  public draw(): void {
    this.handService.draw().subscribe();
  }

  /**
   * Performs a roll action, provided it is the player's turn to act.
   */
  public roll(): void {
    this.handService.roll().subscribe(() => {
    });
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

  /**
   * Updates game data so the summary displayed is accurate.
   */
  private updateGameData(): void {
    this.currentHand = this.gameModel.hands.length - 1;

    if (this.hand.actions.length > 0) {
      const lastActingPlayer = this.hand.actions[this.hand.actions.length - 1].player;
      const lastActingIndex = this.gameModel.players.findIndex(p => p.id === lastActingPlayer.id);
      const card: CardModel = this.hand.actions[this.hand.actions.length - 1].drawnCard;
      this.gameData[lastActingIndex].rolls[this.currentHand] = {
        // value: this.hand.actions[this.hand.actions.length - 1].value,
        value: `${this.valueMapping[card.value]}${this.suitMapping[card.suit]}`,
        winner: false,
        acting: false
      } as RollModel;
    }
  }

  /**
   * Highlights the cell in the game summary that will be filled next. This helps to indicate who needs to act.
   */
  private highlightWaitingToAct(): void {
    let flag = false;
    for (let i = 0; i < this.gameData[0].rolls.length; i++) {
      for (const row of this.gameData) {
        row.rolls[i].acting = false;
      }
    }
    for (let i = 0; i < this.gameData[0].rolls.length; i++) {
      for (const row of this.gameData) {
        if (!flag && row.rolls[i].value === '-1') {
          row.rolls[i].acting = true;
          flag = true;
        }
      }
    }
  }

  /**
   * When a hand is over, the best roll is highlighted in green so it is easy to see who won the hand.
   */
  private highlightBestScore(): void {
    // After the last roll of the hand, we highlight the best score by setting the winner property to true
    if (this.gameModel.hands != null) {
      const lastRound = this.gameModel.hands.length - 2;
      if (lastRound >= 0) {
        this.handService
        .determineWinner({handId: this.gameModel.hands[this.gameModel.hands.length - 2]})
        .subscribe((player: PlayerModel) => {
          // tslint:disable-next-line:prefer-for-of
          for (let i = 0; i < this.gameData.length; i++) {
            this.gameData[i].rolls[lastRound].winner = this.gameData[i].player.id === player.id;
          }
        });
      }
    }
  }

  /**
   * When a hand ends, updates the score column of the game summary.
   */
  private updateScore(): void {
    for (const row of this.gameData) {
      row.player = this.gameModel.players.find(p => p.id === row.player.id);
    }
  }

  /**
   * Filling the game data array with empty strings.
   */
  private initializeGameData(): void {
    this.numbers = Array(this.gameModel.totalHands).fill('').map((v, i) => i + 1);
    for (const player of this.gameModel.players) {
      this.gameData.push({
        player,
        rolls: Array(this.gameModel.totalHands),
        acting: false
      } as PlayerStatModel);
      for (let i = 0; i < this.gameModel.totalHands; i++) {
        this.gameData[this.gameData.length - 1].rolls[i] = {winner: false, acting: false, value: '-1'} as RollModel;
      }
    }
  }
}

/**
 * This will most likely only be a temporary model, so I will leave it in this file for now.
 */
export interface PlayerStatModel {
  player: GamePlayerModel;
  rolls: RollModel[];
  acting: boolean;
}

/**
 * Another model that is most likely temporary, so I will let this one live here for now as well.
 */
export interface RollModel {
  value: string;
  winner: boolean;
  acting: boolean;
}
