import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {GameActionModel, LobbyPlayerModel} from 'src/app/api/models';
import {EmittersService, GameService} from 'src/app/api/services';
import {SseService} from 'src/app/shared/sse.service';
import {AuthService} from 'src/app/shared/auth.service';
import {ToastService} from 'src/app/shared/toast.service';
import {ApiConfiguration} from 'src/app/api/api-configuration';
import {ApiInterceptor} from 'src/app/api-interceptor.service';
import {LobbyDocument} from 'src/app/api/models/lobby-document';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {AppStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {startGame} from '../../state/app.actions';

@Component({
  selector: 'pkr-lobby',
  templateUrl: './lobby.component.html',
  styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent implements OnInit {
  /**
   * Flag that lets the UI know whether the players status is set to ready or not.
   */
  public ready: boolean = false;
  /**
   * Flag that lets the UI know whether the leave game warning should be displayed.
   */
  public displayLeaveWarning: boolean = true;
  /**
   * Flag that lets the UI know whether the can start alert should be displayed.
   */
  public displayCanStartAlert: boolean = true;
  /**
   * Paths to the ready icon assets used to communicate whether the players in the game are ready or not.
   */
  public readyIcons = {
    ready: '../../assets/icons/green_checkmark.svg',
    notReady: '../../assets/icons/red_x.svg'
  };
  /**
   * Path to the crown icon used to communicate who is the host of the game.
   */
  public crownIcon = '../../assets/icons/crown.svg';
  /**
   * Previous value of canStart. Used to determine if canStart has changed when a new lobby document is received.
   */
  private lastCanStart: boolean = false;
  /**
   * Stores the last action that was performed. Used to help determine whether a toast should be displayed or not. The backend will
   * occasionally re-send the same lobby document to prevent the emitter from timing out. In such a case, we don't want to re-display
   * the same toast.
   */
  private lastAction: GameActionModel;

  constructor(
    private apiConfiguration: ApiConfiguration,
    private apiInterceptor: ApiInterceptor,
    private activatedRoute: ActivatedRoute,
    private gameService: GameService,
    private emittersService: EmittersService,
    private router: Router,
    private sseService: SseService,
    public authService: AuthService,
    private toastService: ToastService,
    private store: Store<AppStateContainer>) {
  }

  /**
   * Model representing the lobby.
   */
  public get lobbyModel(): LobbyDocument {
    return this.sseService.lobbyDocument;
  }

  /**
   * Flag that lets the UI know whether the game can be started or not.
   */
  public get canStart(): boolean {
    if (this.lobbyModel.players !== undefined) {
      const canStart: boolean = this.lobbyModel.players
        .find((player: LobbyPlayerModel) => !player.ready) === undefined && this.lobbyModel.players.length > 1;
      if (!this.lastCanStart && canStart) {
        this.displayCanStartAlert = true;
      }
      this.lastCanStart = canStart;
      return canStart;
    }
    return false;
  }

  /**
   * Getter for the model representing the host of the game.
   */
  public get hostModel(): LobbyPlayerModel {
    if (this.lobbyModel == null || this.lobbyModel.players == null) {
      return {host: false} as LobbyPlayerModel;
    } else {
      const hostModel: LobbyPlayerModel = this.lobbyModel.players.find(player => player.id === this.lobbyModel.host);
      return this.lobbyModel.players.find(player => player.id === this.lobbyModel.host);
    }
  }

  /**
   * Getter for the name of the host. Format is '{First Name} {Last Name}'.
   */
  public get host(): string {
    if (this.lobbyModel == null || this.lobbyModel.players == null) {
      return '';
    } else {
      const hostModel: LobbyPlayerModel = this.lobbyModel.players.find(player => player.id === this.lobbyModel.host);
      return `${hostModel.firstName} ${hostModel.lastName}`;
    }
  }

  ngOnInit(): void {
    this.sseService.openEvent(EmitterType.Lobby, () => {
      this.displayToast();
    });
  }

  /**
   * Called when a player is ready for the game to start.
   */
  public sendReadyRequest(): void {
    this.gameService.ready().subscribe(() => this.ready = !this.ready);
  }

  /**
   * Leaves the game by attempting to navigate away from the current page.
   */
  public leaveGame(): void {
    // Only need to leave the page, the leave game guard will handle making the leave game call.
    this.router.navigate(['/join']).then();
    // TODO: See if we can handle this in a more elegant fashion.
  }

  /**
   * Starts the game.
   */
  public startGame(): void {
    this.sseService.closeEvent(EmitterType.Lobby);
    this.store.dispatch(startGame());
  }

  /**
   * Displays a toast when certain events occur. Will only display a toast if the latest action is different from the action that
   * preceded it.
   */
  private displayToast(): void {
    if (this.lobbyModel.gameActions != null && this.lobbyModel.gameActions.length > 0) {
      const currentAction: GameActionModel = this.lobbyModel.gameActions[this.lobbyModel.gameActions.length - 1];

      if (this.lastAction == null || currentAction.id !== this.lastAction.id) {
        this.toastService.show(currentAction.clientMessage, {classname: 'bg-light toast-md', delay: 5000});
      }
      this.lastAction = currentAction;
    }
  }
}
