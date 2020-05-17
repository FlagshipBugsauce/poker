import {Component, HostListener, OnInit, ViewChild} from '@angular/core';
import {PopupComponent, PopupContentModel} from 'src/app/shared/popup/popup.component';
import {Router} from '@angular/router';
import {EmittersService, GameService} from 'src/app/api/services';
import {ApiSuccessModel, GetGameModel} from 'src/app/api/models';
import {SseService} from 'src/app/shared/sse.service';
import {ApiInterceptor} from 'src/app/api-interceptor.service';
import {ApiConfiguration} from 'src/app/api/api-configuration';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';

@Component({
  selector: 'pkr-join',
  templateUrl: './join.component.html',
  styleUrls: ['./join.component.scss']
})
export class JoinComponent implements OnInit {
  /** List of games. */
  private allGames: GetGameModel[] = [];

  // Pagination fields:
  public page: number = 1;
  public pageSize: number = 5;
  public totalGames: number = this.allGames.length;

  /** Popup to confirm player wishes to join the game they clicked on. */
  @ViewChild('popup') public confirmationPopup: PopupComponent;
  /** Content that will appear on the confirmation popup. */
  public popupContent: PopupContentModel[] = [
    {body: ''} as PopupContentModel,
    {body: 'Click cancel if you do not wish to proceed.'} as PopupContentModel
  ] as PopupContentModel[];
  public popupOkCloseProcedure;

  /** Returns a slice of the list of games for pagination. */
  public get games(): any[] {
    return this.allGames
      .map((game: any) => ({...game}))
      .slice((this.page - 1) * this.pageSize, (this.page - 1) * this.pageSize + this.pageSize);
  }

  constructor(
    private apiConfiguration: ApiConfiguration,
    private router: Router,
    private gameService: GameService,
    private emittersService: EmittersService,
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor) { }

  ngOnInit(): void {
    this.sseService
      .getServerSentEvent(
        `${this.apiConfiguration.rootUrl}/emitters/request/${EmitterType.GameList}/${this.apiInterceptor.jwt}`, EmitterType.GameList)
      .subscribe((event: any) => {
        // Using try catch to avoid a bunch of red text in the console. The error is worthless since it's due to JSON.parse.
        const games = this.allGames;
        try {
          this.allGames = JSON.parse(event);
        } catch (err) {
          console.log('Something went wrong with the join game emitter.');
          this.sseService.closeEvent(EmitterType.GameList);
          this.allGames = games;
        }
      });

    this.refreshGameList();
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
   * Requests an updated list of games. This is requested when the client first lands on this page. May also add a button
   * which explicitly calls this function at some point.
   */
  private async refreshGameList(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.emittersService.requestUpdate({ type: EmitterType.GameList }).subscribe(() => { });
  }

  /**
   * Shows the confirmation popup that will take a player to the game they clicked on.
   * @param game GetGameModel with based information about the game.
   */
  public showConfirmationPopup(game: any): void {
    this.popupContent[0].body = `Attempting to join game "${game.name}".`;
    this.popupOkCloseProcedure = () => {
      // Join the lobby.
      this.gameService.joinGame({gameId: game.id}).subscribe((response: ApiSuccessModel) => {
        this.router.navigate([`/game/${game.id}`]);
      });

    };
    this.confirmationPopup.open();
  }
}
