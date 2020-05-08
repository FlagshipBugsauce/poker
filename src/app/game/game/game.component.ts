import { Component, OnInit, HostListener, ViewChild, AfterViewInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { GameService, UsersService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { ApiInterceptor } from 'src/app/api-interceptor.service';
import { GameDocument, ApiSuccessModel, PlayerModel, GameActionModel } from 'src/app/api/models';
import { LobbyComponent } from '../lobby/lobby.component';
import { ApiConfiguration } from 'src/app/api/api-configuration';
import { LeaveGameGuardService } from './leave-game-guard.service';
import { PopupComponent, PopupContentModel } from 'src/app/shared/popup/popup.component';
import { ToastService } from 'src/app/shared/toast.service';

@Component({
  selector: 'pkr-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit, AfterViewInit {
  @ViewChild(LobbyComponent) lobbyComponent: LobbyComponent;
  private gameId: string;

  // TODO: replcae type with proper type.
  public gameModel: GameDocument = { currentGameState: "PostGame", players: [] };
  public canStart: boolean = false;

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
    private activatedRoute: ActivatedRoute, 
    private gameService: GameService, 
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor,
    private toastService: ToastService) { }

  ngAfterViewInit(): void {
    this.leaveGameGuardService.confirmationPopup = this.confirmationPopup;
  }

  ngOnInit(): void {
    this.leaveGameGuardService.canLeave = false;  // Need to set this to false.
    
    this.activatedRoute.paramMap.subscribe((paramMap: ParamMap) => {
      this.gameId = paramMap.get("gameId");
    });

    this.sseService
      .getServerSentEvent(`${this.apiConfiguration.rootUrl}/game/emitter/game/${this.apiInterceptor.jwt}`, "game")
      .subscribe((event: any) => {
        try {
          this.gameModel = <GameDocument> JSON.parse(event);
        } catch(err) {
          console.log("Something went wrong with the emitter.");
        }
        
        this.checkIfGameCanStart();
        
        // Display a toast if appropriate:
        this.displayToast();
      });
    this.refreshGameModel();
  }

  private checkIfGameCanStart() {
    let canStart = this.canStart;
    this.canStart = this.gameModel.players.find((player: PlayerModel) => player.ready == false) == undefined;
    if (!canStart && this.canStart && this.lobbyComponent != null) {
      this.lobbyComponent.displayCanStartAlert = true;
    }
  }

  private displayToast(): void {
    let lastAction: GameActionModel = this.gameModel.gameActions[this.gameModel.gameActions.length - 1];
    if (lastAction != undefined &&
      (lastAction.gameAction == 'Join' || lastAction.gameAction == 'Leave' || lastAction.gameAction == 'Ready')) {
      this.toastService.show(lastAction.clientMessage, { classname: 'bg-light toast-md', delay: 5000 });
    }
    // console.log(lastAction);
    // this.toastService.show(lastAction.clientMessage, { classname: 'bg-light toast-lg', delay: 5000 });
  }

  /** 
   * Ensures that the user "leaves" the game if they leave the page (by refreshing, closing tab, or going to another
   * website). 
   */
  @HostListener('window:beforeunload', ['$event'])
  public userLeftPage($event: any): void {
    this.sseService.closeEvent("game");
    this.gameService.leaveLobby({ Authorization: null }).subscribe((result: ApiSuccessModel) => { });
  }

  /** 
   * Retrieves an updated game document. Typically called right after joining. May add a button to allow client to refresh,
   * which would use this as well.
   */
  private async refreshGameModel(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.gameService.getGameDocumentUpdate({ Authorization: null }).subscribe(() => {

    });
  }
}
