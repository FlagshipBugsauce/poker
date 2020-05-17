import {AfterViewInit, Component, HostListener, OnInit, ViewChild} from '@angular/core';
import {EmittersService, GameService} from 'src/app/api/services';
import {SseService} from 'src/app/shared/sse.service';
import {ApiInterceptor} from 'src/app/api-interceptor.service';
import {ApiSuccessModel, GameActionModel, GameDocument} from 'src/app/api/models';
import {LobbyComponent} from '../lobby/lobby.component';
import {ApiConfiguration} from 'src/app/api/api-configuration';
import {LeaveGameGuardService} from './leave-game-guard.service';
import {PopupComponent, PopupContentModel} from 'src/app/shared/popup/popup.component';
import {ToastService} from 'src/app/shared/toast.service';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {GameState} from 'src/app/shared/models/game-state.enum';

@Component({
  selector: 'pkr-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, AfterViewInit {
  /** Reference to the lobby component. */
  @ViewChild(LobbyComponent) lobbyComponent: LobbyComponent;

  /** The model representing the state of the game at any given point in time. */
  public gameModel: GameDocument;

  /** Flag that is used to determine whether or not the host can start the game. */
  public canStart: boolean = false;

  public popupContent: PopupContentModel[] = [
    {body: 'You will be removed from the game if you leave this page.'} as PopupContentModel,
    {body: 'Click Ok to continue.'} as PopupContentModel
  ] as PopupContentModel[];

  /**
   * Popup that will appear if a player clicks on a link to warn them that they will be removed from the game
   * if they navigate to another page.
   */
  @ViewChild('popup') public confirmationPopup: PopupComponent;
  /** Stores the last action that took place in the game. */
  private lastAction: GameActionModel = {id: null} as GameActionModel;
  public confirmLeavePageProcedure = () => true;

  constructor(
    private leaveGameGuardService: LeaveGameGuardService,
    private apiConfiguration: ApiConfiguration,
    private gameService: GameService,
    private emittersService: EmittersService,
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor,
    private toastService: ToastService) {
  }

  ngAfterViewInit(): void {
    this.leaveGameGuardService.confirmationPopup = this.confirmationPopup;
  }

  ngOnInit(): void {
    this.leaveGameGuardService.canLeave = false;  // Need to set this to false when page loads.

    this.sseService
    .getServerSentEvent(
      `${this.apiConfiguration.rootUrl}/emitters/request/${EmitterType.Game}/${this.apiInterceptor.jwt}`, EmitterType.Game)
      .subscribe((event: any) => {
        try {
          this.gameModel = JSON.parse(event) as GameDocument;

          // Update state in leave game guard:
          this.leaveGameGuardService.gameState = this.gameModel.state as GameState;
          console.log(this.leaveGameGuardService.gameState);
        } catch (err) {
          console.log('Something went wrong with the game emitter.');
          this.sseService.closeEvent(EmitterType.Game);
        }
      });

    this.refreshGameModel().then();
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
      this.gameService.leaveLobby().subscribe((result: ApiSuccessModel) => { });
    }
  }

  /**
   * Retrieves an updated game document. Typically called right after joining. May add a button to allow client to refresh,
   * which would use this as well.
   */
  private async refreshGameModel(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.emittersService.requestUpdate({ type: EmitterType.Game }).subscribe(() => { });
  }
}
