import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { GameDocument, UserModel, PlayerModel, ApiSuccessModel } from 'src/app/api/models';
import { GameService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { AuthService } from 'src/app/shared/auth.service';
import { ToastService } from 'src/app/shared/toast.service';

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
  public displayLeaveWarning: boolean = true;
  public displayCanStartAlert: boolean = true;

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

  public readyIcons = {
    ready: "../../assets/icons/green_checkmark.svg",
    notReady: "../../assets/icons/red_x.svg"
  }
  public crownIcon = "../../assets/icons/crown.svg";

  constructor(
    private activatedRoute: ActivatedRoute, 
    private gameService: GameService,
    private router: Router,
    private sseService: SseService,
    public authService: AuthService,
    private toastService: ToastService) { }

  ngOnInit(): void {
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
}
