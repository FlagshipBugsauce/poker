import { Component, OnInit, ViewChild, HostListener } from '@angular/core';
import { PopupComponent, PopupContentModel } from 'src/app/shared/popup/popup.component';
import { Router } from '@angular/router';
import { GameService } from 'src/app/api/services';
import { GetGameModel, ApiSuccessModel } from 'src/app/api/models';
import { SseService } from 'src/app/shared/sse.service';
import { ApiInterceptor } from 'src/app/api-interceptor.service';
import { ApiConfiguration } from 'src/app/api/api-configuration';

@Component({
  selector: 'pkr-join',
  templateUrl: './join.component.html',
  styleUrls: ['./join.component.scss']
})
export class JoinComponent implements OnInit {
  // TODO: Remove when backend can return games.
  // private _games = sampleGames;
  private _games: GetGameModel[] = [];

  // Pagination fields:
  public page: number = 1;
  public pageSize: number = 5;
  public totalGames: number = this._games.length;

  @ViewChild('popup') public confirmationPopup: PopupComponent;
  public popupContent: PopupContentModel[] = <PopupContentModel[]> [
    <PopupContentModel> { body: "" },
    <PopupContentModel> { body: "Click cancel if you do not wish to proceed." }
  ];
  public popupOkCloseProcedure: Function = () => { };

  /** Returns a slice of the list of games for pagination. */
  public get games(): any[] {
    return this._games
      .map((game: any) => ({...game}))
      .slice((this.page - 1) * this.pageSize, (this.page - 1) * this.pageSize + this.pageSize);
  }

  constructor(
    private apiConfiguration: ApiConfiguration,
    private router: Router,
    private gameService: GameService,
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor) { }

  ngOnInit(): void {
    this.sseService
      .getServerSentEvent(`${this.apiConfiguration.rootUrl}/game/emitter/join/${this.apiInterceptor.jwt}`, "joinGame")
      .subscribe((event: any) => {
        // Using try catch to avoid a bunch of red text in the console. The error is worthless since it's due to JSON.parse.
        try {
          this._games = JSON.parse(event);
        } catch(err) {
          console.log("Something went wrong with the join game emitter.");
        }
      });
    
    this.refreshGameList();
  }

  @HostListener('window:beforeunload', ['$event'])
  public userLeftPage($event: any): void {
    this.sseService.closeEvent("joinGame");
    this.gameService.destroyJoinGameEmitter({ Authorization: null }).subscribe(() => { });
  }

  private async refreshGameList(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.gameService.refreshGameList({ Authorization: null }).subscribe((response: ApiSuccessModel) => { });
  }

  /**
   * Shows the confirmation popup that will take a player to the game they clicked on.
   * @param game GetGameModel with based information about the game.
   */
  public showConfirmationPopup(game: any): void {
    this.popupContent[0].body = `Attempting to join game "${game.name}".`;
    this.popupOkCloseProcedure = () => {
      // Join the lobby.
      this.gameService.joinGame({ gameId: game.id, Authorization: null }).subscribe((response: ApiSuccessModel) => {
        this.router.navigate([`/game/${game.id}`]);
      });
      
    }
    this.confirmationPopup.open();
  }
}

// Sample data for table.
export const sampleGames: any[] = [
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Poker with Randy McGee",
    host: "Randy McGee",
    currentPlayers: 2,
    maxPlayers: 6,
    buyIn: 69
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Justin's Summer Sunday Smokefest",
    host: "Justin Stephenson",
    currentPlayers: 6,
    maxPlayers: 9,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "big shots poker",
    host: "Billy Bob McGraw",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "YorkU Poker Tournament",
    host: "Prof. Jeff Edmonds",
    currentPlayers: 1,
    maxPlayers: 10,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Thinking Inductively with A. Mirzaian",
    host: "Andranik Mirzaian",
    currentPlayers: 5,
    maxPlayers: 6,
    buyIn: 69
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "design by contract",
    host: "Jackie Wang DPhil, Oxon",
    currentPlayers: 6,
    maxPlayers: 9,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Poker with Randy McGee",
    host: "Randy McGee",
    currentPlayers: 2,
    maxPlayers: 6,
    buyIn: 69
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Justin's Summer Sunday Smokefest",
    host: "Justin Stephenson",
    currentPlayers: 6,
    maxPlayers: 9,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "big shots poker",
    host: "Billy Bob McGraw",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "YorkU Poker Tournament",
    host: "Prof. Jeff Edmonds",
    currentPlayers: 1,
    maxPlayers: 10,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Thinking Inductively with A. Mirzaian",
    host: "Andranik Mirzaian",
    currentPlayers: 5,
    maxPlayers: 6,
    buyIn: 69
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "design by contract",
    host: "Jackie Wang DPhil, Oxon",
    currentPlayers: 6,
    maxPlayers: 9,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  }
]
