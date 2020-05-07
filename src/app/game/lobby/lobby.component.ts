import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { GameDocument, UserModel, PlayerModel, ApiSuccessModel } from 'src/app/api/models';
import { GameService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { AuthService } from 'src/app/shared/auth.service';

@Component({
  selector: 'pkr-lobby',
  templateUrl: './lobby.component.html',
  styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent implements OnInit {
  // TODO: replace typep with proper game model type once backend models are generated.
  @Input('gameModel') public gameModel: GameDocument;
  @Input('canStart') public canStart: boolean;
  public ready: boolean = false;

  public get hostModel(): PlayerModel {
    if (this.gameModel == null || this.gameModel.players == null) {
      return <PlayerModel> { host: false };
    } else {
      let hostModel: PlayerModel = this.gameModel.players.find(player => player.id == this.gameModel.host);
      return this.gameModel.players.find(player => player.id == this.gameModel.host);
    }
  }

  public get host(): string {
    if (this.gameModel == null || this.gameModel.players == null) {
      return "";
    } else {
      let hostModel: PlayerModel = this.gameModel.players.find(player => player.id == this.gameModel.host);
      return `${hostModel.firstName} ${hostModel.lastName}`;
    }
  }

  public samplePlayers = samplePlayers;
  public readyIcons = {
    ready: "../../assets/icons/green_checkmark.svg",
    notReady: "../../assets/icons/red_x.svg"
  }
  public crownIcon = "../../assets/icons/crown.svg";
  public hostId = "6f5924ca-5c79-418f-89bf-5a3e2bc248cc";
  // TODO: need to implement an endpoint to get current users info
  public clientUserId = "6f5924ca-5c79-418f-89bf-5a3e2bc248cc";

  constructor(
    private activatedRoute: ActivatedRoute, 
    private gameService: GameService,
    private router: Router,
    private sseService: SseService,
    public authService: AuthService) { }

  ngOnInit(): void {
    console.log(this.gameModel);
  }

  /**
   * Called when a player is ready for the game to start.
   */
  public sendReadyRequest(): void {
    this.gameService.ready({ Authorization: null }).subscribe((result: ApiSuccessModel) => {
      this.ready = true;
    });
  }

  public leaveGame(): void {
    this.sseService.closeEvent("game");
    this.gameService.leaveLobby({ Authorization: null }).subscribe((result: ApiSuccessModel) => {
      this.router.navigate(['/join']);
    });
  }
}

export const samplePlayers = [
  { 
    id: "6f5924ca-5c79-418f-89bf-5a3e2bc248cc",
    name: "Jimmy McGillicutty",
    ready: true
  },
  { 
    id: "9a2b7bc1-2a56-4bb1-b2bb-936b30c60771",
    name: "Jon Gourley",
    ready: false
  },
  { 
    id: "5386f8a3-45d5-4b07-83ea-62579edfa831",
    name: "Billy Bob McGraw",
    ready: true
  },
  { 
    id: "039c41e8-15e0-4423-8b25-b7b804736592",
    name: "Jeffrey Edmonds",
    ready: true
  },
  { 
    id: "3df981c2-dd7a-43ea-854e-a34e53b61cd1",
    name: "Andranik Mirzaian",
    ready: false
  },
  { 
    id: "1e6fece1-ad09-477e-b63e-4e71f376ee53",
    name: "Jackie Want",
    ready: false
  },
];
