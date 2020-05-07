import { Component, OnInit, HostListener, ViewChild, AfterViewInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { GameService, UsersService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { ApiInterceptor } from 'src/app/api-interceptor.service';
import { GameDocument, ApiSuccessModel, PlayerModel, GameActionModel } from 'src/app/api/models';
import { samplePlayers, LobbyComponent } from '../lobby/lobby.component';
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

export const sampleGameModel = <GameDocument> {
  id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
  currentGameState: "PreGame",
  host: "Jimmy McGillicutty",
  players: [
    "6f5924ca-5c79-418f-89bf-5a3e2bc248cc",
    "9a2b7bc1-2a56-4bb1-b2bb-936b30c60771",    
    "5386f8a3-45d5-4b07-83ea-62579edfa831",    
    "039c41e8-15e0-4423-8b25-b7b804736592",    
    "3df981c2-dd7a-43ea-854e-a34e53b61cd1",    
    "1e6fece1-ad09-477e-b63e-4e71f376ee53",
  ],
  gameActions: null,
  maxPlayers: 10,
  buyIn: 420,
  name: "Justin's Summer Sunday Smokeout For Smokers"
}
