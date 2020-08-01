import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ClientUserModel, LobbyModel, LobbyPlayerModel} from 'src/app/api/models';
import {AppStateContainer, LobbyStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {notReady, readyUp, startGame} from '../../state/app.actions';
import {selectLobbyModel, selectLoggedInUser, selectReadyStatus} from '../../state/app.selector';
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
  /** Observable used to determine whether a player is ready or not. */
  public ready$: Observable<boolean>;
  /** Observable of the model for the user currently logged in. */
  public userModel$: Observable<ClientUserModel>;
  /** Model representing the lobby. */
  public lobbyModel: LobbyModel;
  /** Helper subject which assists in terminating subscriptions. */
  public ngDestroyed$ = new Subject();
  /**
   * Previous value of canStart. Used to determine if canStart has changed when a new lobby document
   * is received.
   */
  private lastCanStart: boolean = false;

  constructor(
    private activatedRoute: ActivatedRoute,
    private router: Router,
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

  /** Getter for the name of the host. Format is '{First Name} {Last Name}'. */
  public get host(): string {
    return this.lobbyModel ? this.lobbyModel.host ?
      `${this.lobbyModel.host.firstName} ${this.lobbyModel.host.lastName}` : '' : '';
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.appStore.dispatch(notReady());
  }

  public ngOnInit(): void {
    this.ready$ = this.appStore.select(selectReadyStatus);
    this.lobbyStore.select(selectLobbyModel)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((lobbyModel: LobbyModel) => this.lobbyModel = lobbyModel);
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
}
