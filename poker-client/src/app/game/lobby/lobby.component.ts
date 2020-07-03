import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {GameActionModel, LobbyPlayerModel, UserModel} from 'src/app/api/models';
import {ToastService} from 'src/app/shared/toast.service';
import {LobbyDocument} from 'src/app/api/models/lobby-document';
import {AppStateContainer, LobbyStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {notReady, readyUp, startGame} from '../../state/app.actions';
import {selectLobbyDocument, selectLoggedInUser, selectReadyStatus} from '../../state/app.selector';
import {Observable, Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';
import {APP_ROUTES} from '../../app-routes';

@Component({
  selector: 'pkr-lobby',
  templateUrl: './lobby.component.html',
  styleUrls: ['./lobby.component.scss']
})
export class LobbyComponent implements OnInit, OnDestroy {
  /** Flag that lets the UI know whether the leave game warning should be displayed. */
  public displayLeaveWarning: boolean = true;

  /** Flag that lets the UI know whether the can start alert should be displayed. */
  public displayCanStartAlert: boolean = true;

  /**
   * Paths to the ready icon assets used to communicate whether the players in the game are ready or
   * not.
   */
  public readyIcons = {
    ready: 'assets/icons/green_checkmark.svg',
    notReady: 'assets/icons/red_x.svg'
  };

  /** Path to the crown icon used to communicate who is the host of the game. */
  public crownIcon = 'assets/icons/crown.svg';

  /**
   * Previous value of canStart. Used to determine if canStart has changed when a new lobby document
   * is received.
   */
  private lastCanStart: boolean = false;

  /**
   * Stores the last action that was performed. Used to help determine whether a toast should be
   * displayed or not. The backend will occasionally re-send the same lobby document to prevent the
   * emitter from timing out. In such a case, we don't want to re-display the same toast.
   */
  private lastAction: GameActionModel;

  /** Observable used to determine whether a player is ready or not. */
  public ready$: Observable<boolean>;

  /** Observable of the model for the user currently logged in. */
  public userModel$: Observable<UserModel>;

  /** Model representing the lobby. */
  public lobbyModel: LobbyDocument;

  /** Helper subject which assists in terminating subscriptions. */
  public ngDestroyed$ = new Subject();

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private toastService: ToastService,
    private appStore: Store<AppStateContainer>,
    private lobbyStore: Store<LobbyStateContainer>) {
  }

  /** Flag that lets the UI know whether the game can be started or not. */
  public get canStart(): boolean {
    if (this.lobbyModel.players !== undefined) {
      const canStart: boolean = this.lobbyModel.players
        .find((player: LobbyPlayerModel) =>
          !player.ready) === undefined && this.lobbyModel.players.length > 1;
      if (!this.lastCanStart && canStart) {
        this.displayCanStartAlert = true;
      }
      this.lastCanStart = canStart;
      return canStart;
    }
    return false;
  }

  /** Getter for the model representing the host of the game. */
  public get hostModel(): LobbyPlayerModel {
    return this.lobbyModel && this.lobbyModel.players ?
      this.lobbyModel.players.find(player => player.id === this.lobbyModel.host) :
      {host: false} as LobbyPlayerModel;
  }

  /** Getter for the name of the host. Format is '{First Name} {Last Name}'. */
  public get host(): string {
    if (this.lobbyModel && this.lobbyModel.players) {
      const hostModel: LobbyPlayerModel =
        this.lobbyModel.players.find(player => player.id === this.lobbyModel.host);
      return `${hostModel.firstName} ${hostModel.lastName}`;
    } else {
      return '';
    }
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.appStore.dispatch(notReady());
  }

  public ngOnInit(): void {
    this.ready$ = this.appStore.select(selectReadyStatus);
    this.lobbyStore.select(selectLobbyDocument)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((lobbyDocument: LobbyDocument) => {
        this.lobbyModel = lobbyDocument;
        this.displayToast();
      });
    this.userModel$ = this.appStore.select(selectLoggedInUser);
  }

  /** Called when a player is ready for the game to start. */
  public sendReadyRequest(): void {
    this.appStore.dispatch(readyUp());
  }

  /** Leaves the game by attempting to navigate away from the current page. */
  public leaveGame(): void {
    // Only need to leave the page, the leave game guard will handle making the leave game call.
    this.router.navigate([`/${APP_ROUTES.JOIN_GAME}`]).then();
  }

  /** Starts the game. */
  public startGame(): void {
    this.appStore.dispatch(startGame());
  }

  /**
   * Displays a toast when certain events occur. Will only display a toast if the latest action is
   * different from the action that preceded it.
   */
  private displayToast(): void {
    if (this.lobbyModel.gameActions != null && this.lobbyModel.gameActions.length > 0) {
      const currentAction: GameActionModel =
        this.lobbyModel.gameActions[this.lobbyModel.gameActions.length - 1];
      if (this.lastAction == null || currentAction.id !== this.lastAction.id) {
        this.toastService.show(
          currentAction.clientMessage, {classname: 'bg-light toast-md', delay: 5000});
      }
      this.lastAction = currentAction;
    }
  }
}
