import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PlayerModel, ApiSuccessModel, GameActionModel } from 'src/app/api/models';
import { GameService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { AuthService } from 'src/app/shared/auth.service';
import { ToastService } from 'src/app/shared/toast.service';
import { ApiConfiguration } from 'src/app/api/api-configuration';
import { ApiInterceptor } from 'src/app/api-interceptor.service';
import { LobbyDocument } from 'src/app/api/models/lobby-document';
import { EmitterType } from 'src/app/shared/models/emitter-type.model';

@Component({
  selector: 'pkr-lobby',
  templateUrl: './lobby.component.html',
  styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent implements OnInit {
  public lobbyModel: LobbyDocument = <LobbyDocument> { };

  public canStart: boolean;
  public ready: boolean = false;
  public displayLeaveWarning: boolean = true;
  public displayCanStartAlert: boolean = true;

  private lastAction: GameActionModel;

  public get hostModel(): PlayerModel {
    if (this.lobbyModel == null || this.lobbyModel.players == null) {
      return <PlayerModel> { host: false };
    } else {
      let hostModel: PlayerModel = this.lobbyModel.players.find(player => player.id == this.lobbyModel.host);
      return this.lobbyModel.players.find(player => player.id == this.lobbyModel.host);
    }
  }

  public get host(): string {
    if (this.lobbyModel == null || this.lobbyModel.players == null) {
      return "";
    } else {
      let hostModel: PlayerModel = this.lobbyModel.players.find(player => player.id == this.lobbyModel.host);
      return `${hostModel.firstName} ${hostModel.lastName}`;
    }
  }

  public readyIcons = {
    ready: "../../assets/icons/green_checkmark.svg",
    notReady: "../../assets/icons/red_x.svg"
  }
  public crownIcon = "../../assets/icons/crown.svg";

  constructor(
    private apiConfiguration: ApiConfiguration,
    private apiInterceptor: ApiInterceptor,
    private activatedRoute: ActivatedRoute, 
    private gameService: GameService,
    private router: Router,
    private sseService: SseService,
    public authService: AuthService,
    private toastService: ToastService) { }

  ngOnInit(): void {
    this.sseService
      .getServerSentEvent(`${this.apiConfiguration.rootUrl}/game/emitter/lobby/${this.apiInterceptor.jwt}`, EmitterType.Lobby)
      .subscribe((event: any) => {
        try {
          this.lobbyModel = <LobbyDocument> JSON.parse(event);
        } catch(err) {
          console.log("Something went wrong with the emitter.");
          this.sseService.closeEvent(EmitterType.Lobby);
        }
        
        this.checkIfGameCanStart();
        this.displayToast();
      });
    
    this.refreshLobbyModel();
  }

  /**
   * Displays a toast when certain events occur.
   */
  private displayToast(): void {
    if (this.lobbyModel != null && this.lobbyModel.gameActions != null && this.lobbyModel.gameActions.length > 0) {
      let currentAction: GameActionModel = this.lobbyModel.gameActions[this.lobbyModel.gameActions.length - 1];

      if (this.lastAction == null || currentAction.id != this.lastAction.id) {
        this.toastService.show(currentAction.clientMessage, { classname: 'bg-light toast-md', delay: 5000 });
      }
      this.lastAction = currentAction;
    }
  }

  /**
   * Helper to determine when to show the alert notifying the host that they may start the game.
   */
  private checkIfGameCanStart(): void {
    let oldCanStart = this.canStart;
    this.canStart = this.lobbyModel.players
      .find((player: PlayerModel) => !player.ready) == undefined && this.lobbyModel.players.length > 1;

    if (!oldCanStart && this.canStart) {  // If we went from not being able to start -> being able to start, show the alert
      this.displayCanStartAlert = true;
    }
  }

  /**
   * Refreshes the lobby model by requesting an updated copy be sent via the SSE emitter.
   */
  private async refreshLobbyModel(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.gameService.getLobbyDocumentUpdate({ Authorization: null }).subscribe(() => { });
  }

  /**
   * Called when a player is ready for the game to start.
   */
  public sendReadyRequest(): void {
    this.gameService.ready({ Authorization: null }).subscribe((result: ApiSuccessModel) => {
      this.ready = true;
    });
  }

  /**
   * Leaves the game by attempting to navigate away from the current page.
   */
  public leaveGame(): void {
    // Only need to leave the page, the leave game guard will handle making the leave game call.
    this.router.navigate(['/join']);
  }

  public startGame(): void {
    this.sseService.closeEvent(EmitterType.Lobby);
    this.gameService.startGame({ Authorization: null }).subscribe(() => { });
  }
}
