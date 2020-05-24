import {AfterViewInit, Component, HostListener, OnInit, ViewChild} from '@angular/core';
import {EmittersService, GameService} from 'src/app/api/services';
import {SseService} from 'src/app/shared/sse.service';
import {ApiSuccessModel, GameDocument} from 'src/app/api/models';
import {LobbyComponent} from '../lobby/lobby.component';
import {ApiConfiguration} from 'src/app/api/api-configuration';
import {LeaveGameGuardService} from './leave-game-guard.service';
import {PopupComponent, PopupContentModel} from 'src/app/shared/popup/popup.component';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {GameState} from 'src/app/shared/models/game-state.enum';
import {PlayComponent, PlayerStatModel} from '../play/play.component';

@Component({
  selector: 'pkr-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, AfterViewInit {
  /**
   * Reference to the lobby component.
   */
  @ViewChild(LobbyComponent) lobbyComponent: LobbyComponent;

  /**
   * Reference to the play component.
   */
  @ViewChild(PlayComponent) playComponent: PlayComponent;

  /**
   * Popup that will appear if a player clicks on a link to warn them that they will be removed from the game
   * if they navigate to another page.
   */
  @ViewChild('popup') public confirmationPopup: PopupComponent;

  /**
   * Flag that informs the UI when the game has ended.
   */
  public gameOver: boolean = false;

  /**
   * Flag that informs the UI when the game is in play.
   */
  public inPlay: boolean = false;

  /**
   * A summary of what occurred in the game.
   */
  public gameData: PlayerStatModel[] = [] as PlayerStatModel[];
  /**
   * Content for the popup that appears when leaving the page (except when refreshing or going to external site).
   */
  public popupContent: PopupContentModel[] = [
    {body: 'You will be removed from the game if you leave this page.'} as PopupContentModel,
    {body: 'Click Ok to continue.'} as PopupContentModel
  ] as PopupContentModel[];

  constructor(
    private leaveGameGuardService: LeaveGameGuardService,
    private apiConfiguration: ApiConfiguration,
    private gameService: GameService,
    private emittersService: EmittersService,
    private sseService: SseService) {
  }

  /** The model representing the state of the game at any given point in time. */
  public get gameModel(): GameDocument {
    return this.sseService.gameDocument;
  }

  ngAfterViewInit(): void {
    this.leaveGameGuardService.confirmationPopup = this.confirmationPopup;
  }

  ngOnInit(): void {
    this.gameOver = false;
    this.leaveGameGuardService.canLeave = false;  // Need to set this to false when page loads.
    this.sseService.openEvent(EmitterType.Game, () => {
      if (this.gameModel.state === 'Over') {
        this.updateGameOver().then();
      }
      if (this.gameModel.state === 'Play') {
        this.inPlay = true;
      }
    });
  }

  /**
   * Ensures that the user "leaves" the game if they leave the page (by refreshing, closing tab, or going to another
   * website).
   */
  @HostListener('window:beforeunload', ['$event'])
  public userLeftPage($event: any): void {
    this.sseService.closeEvent(EmitterType.Game);
    this.sseService.closeEvent(EmitterType.Lobby);
    this.sseService.closeEvent(EmitterType.Hand);
    if (this.gameModel.state === GameState.Lobby) {
      this.gameService.leaveLobby().subscribe((result: ApiSuccessModel) => {
      });
    }
  }

  /**
   * Helper which adds a small delay after the game state transitions, to ensure any calculations that need to be done in the play
   * component have time to complete before the component is hidden. Not having this was causing issues.
   */
  private async updateGameOver(): Promise<void> {
    for (const psm of this.playComponent.gameData) {
      this.gameData.push(psm);
    }
    await new Promise(resolve => setTimeout(resolve, 1000));
    this.inPlay = false;
    this.gameOver = true;
  }
}
