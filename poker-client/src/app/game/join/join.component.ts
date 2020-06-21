import {Component, HostListener, OnInit, ViewChild} from '@angular/core';
import {PopupComponent, PopupContentModel} from 'src/app/shared/popup/popup.component';
import {Router} from '@angular/router';
import {EmittersService, GameService} from 'src/app/api/services';
import {ApiSuccessModel, GetGameModel} from 'src/app/api/models';
import {SseService} from 'src/app/shared/sse.service';
import {ApiConfiguration} from 'src/app/api/api-configuration';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {AppStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {joinLobby} from '../../state/app.actions';

@Component({
  selector: 'pkr-join',
  templateUrl: './join.component.html',
  styleUrls: ['./join.component.scss']
})
export class JoinComponent implements OnInit {
  /**
   * The current page of the game list being displayed.
   */
  public page: number = 1;

  /**
   * The current number of games being displayed per page.
   */
  public pageSize: number = 5;

  /**
   * The total games in the list of games.
   */
  public totalGames: number = this.sseService.gameList.length;

  /**
   * Popup to confirm player wishes to join the game they clicked on.
   */
  @ViewChild('popup') public confirmationPopup: PopupComponent;

  /**
   * Content that will appear on the confirmation popup.
   */
  public popupContent: PopupContentModel[] = [
    {body: ''} as PopupContentModel,
    {body: 'Click cancel if you do not wish to proceed.'} as PopupContentModel
  ] as PopupContentModel[];

  /**
   * Procedure to be executed when the OK button is clicked on the popup.
   */
  public popupOkCloseProcedure: () => void;

  constructor(
    private apiConfiguration: ApiConfiguration,
    private router: Router,
    private gameService: GameService,
    private emittersService: EmittersService,
    private sseService: SseService,
    private store: Store<AppStateContainer>) {
  }

  /** Returns a slice of the list of games for pagination. */
  public get games(): GetGameModel[] {
    return this.sseService.gameList
      .map((game: GetGameModel) => ({...game}))
      .slice((this.page - 1) * this.pageSize, (this.page - 1) * this.pageSize + this.pageSize);
  }

  ngOnInit(): void {
    this.sseService.openEvent(EmitterType.GameList);
  }

  /**
   * Destroys the emitter that provides updated lists of games before leaving the page to avoid errors on the backend.
   * @param $event Before unload event.
   */
  @HostListener('window:beforeunload', ['$event'])
  public userLeftPage($event: any): void {
    this.sseService.closeEvent(EmitterType.GameList);
  }

  /**
   * Shows the confirmation popup that will take a player to the game they clicked on.
   * @param game GetGameModel with based information about the game.
   */
  public showConfirmationPopup(game: GetGameModel): void {
    this.popupContent[0].body = `Attempting to join game "${game.name}".`;
    this.popupOkCloseProcedure = () => {
      // Join the lobby.
      this.gameService.joinGame({gameId: game.id}).subscribe(() => {
        this.router.navigate([`/game/${game.id}`]).then();
        this.store.dispatch(joinLobby({id: game.id, name: game.name, host: game.host}));
      });
    };
    this.confirmationPopup.open();
  }
}
