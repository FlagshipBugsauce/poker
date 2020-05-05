import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { GameService, UsersService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { ApiInterceptor } from 'src/app/api-interceptor.service';
import { GameDocument, UserModel, ApiSuccessModel } from 'src/app/api/models';

@Component({
  selector: 'pkr-game',
  templateUrl: './game.component.html',
  styleUrls: ['./game.component.scss']
})
export class GameComponent implements OnInit {
  private gameId: string;

  // TODO: replcae type with proper type.
  public gameModel: GameDocument = { currentGameState: "PostGame", players: [] };
  // public players: UserModel[];
  
  constructor(
    private activatedRoute: ActivatedRoute, 
    private gameService: GameService, 
    private usersService: UsersService,
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor) { }

  ngOnInit(): void {
    this.activatedRoute.paramMap.subscribe((paramMap: ParamMap) => {
      this.gameId = paramMap.get("gameId");
      console.log(this.gameId);
    });

    // TODO: Call backend to find the game model
    //this.gameModel = sampleGameModel;
    // this.gameService.getGameEmitter({ Authorization: null, gameId: this.gameId}).subscribe((data) => {
    //   console.log(data);
    // });
    this.sseService
      .getServerSentEvent(`http://localhost:8080/game/emitter/${this.apiInterceptor.jwt}`)
      .subscribe((event: any) => {
        console.log(JSON.parse(event));
        this.gameModel = <GameDocument> JSON.parse(event);
        // this.updatePlayers();
      });
    this.refreshGameModel();
  }

  private async refreshGameModel(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.gameService.getGameDocumentUpdate({ Authorization: null }).subscribe((response) => {

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
