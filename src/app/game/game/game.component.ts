import { Component, OnInit, HostListener, ViewChild, AfterViewInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { GameService, UsersService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { ApiInterceptor } from 'src/app/api-interceptor.service';
import { GameDocument as LobbyDocument, ApiSuccessModel, PlayerModel, GameActionModel, GameDocument } from 'src/app/api/models';
import { LobbyComponent } from '../lobby/lobby.component';
import { ApiConfiguration } from 'src/app/api/api-configuration';
import { LeaveGameGuardService } from './leave-game-guard.service';
import { PopupComponent, PopupContentModel } from 'src/app/shared/popup/popup.component';
import { ToastService } from 'src/app/shared/toast.service';
import { EmitterType } from 'src/app/shared/models/emitter-type.model';

@Component({
  selector: 'pkr-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, AfterViewInit {
  /** Reference to the lobby component. */
  @ViewChild(LobbyComponent) lobbyComponent: LobbyComponent;

  /** The model representing the state of the game at any given point in time. */
  public gameModel: GameDocument = <GameDocument> { state: "PostGame" };

  /** Flag that is used to determine whether or not the host can start the game. */
  public canStart: boolean = false;

  /** Stores the last action that took place in the game. */
  private lastAction: GameActionModel = <GameActionModel> { id: null };

  /**
   * Popup that will appear if a player clicks on a link to warn them that they will be removed from the game
   * if they navigate to another page.
   */
  @ViewChild('popup') public confirmationPopup: PopupComponent;
  public popupContent: PopupContentModel[] = <PopupContentModel[]> [
    <PopupContentModel> { body: "You will be removed from the game if you leave this page." },
    <PopupContentModel> { body: "Click Ok to continue." }
  ];
  public confirmLeavePageProcedure: Function = () => true;
  
  constructor(
    private leaveGameGuardService: LeaveGameGuardService,
    private apiConfiguration: ApiConfiguration,
    private gameService: GameService, 
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor,
    private toastService: ToastService) { }

  ngAfterViewInit(): void {
    this.leaveGameGuardService.confirmationPopup = this.confirmationPopup;
  }

  ngOnInit(): void {
    this.leaveGameGuardService.canLeave = false;  // Need to set this to false when page loads.

    this.sseService
      .getServerSentEvent(`${this.apiConfiguration.rootUrl}/game/emitter/game/${this.apiInterceptor.jwt}`, EmitterType.Game)
      .subscribe((event: any) => {
        try {
          this.gameModel = <GameDocument> JSON.parse(event);
        } catch(err) {
          console.log("Something went wrong with the emitter.");
          this.sseService.closeEvent(EmitterType.Game);
        }
      });

    this.refreshGameModel();
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
    this.gameService.leaveLobby({ Authorization: null }).subscribe((result: ApiSuccessModel) => { });
  }

  /** 
   * Retrieves an updated game document. Typically called right after joining. May add a button to allow client to refresh,
   * which would use this as well.
   */
  private async refreshGameModel(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.gameService.getGameDocumentUpdate({ Authorization: null }).subscribe(() => { });
  }
}
