import {Component, Input, OnInit} from '@angular/core';
import {DrawGameDataModel, GameDocument} from 'src/app/api/models';
import {EmittersService, GameService} from 'src/app/api/services';
import {SseService} from 'src/app/shared/sse.service';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';

@Component({
  selector: 'pkr-end',
  templateUrl: './end.component.html',
  styleUrls: ['./end.component.scss']
})
export class EndComponent implements OnInit {
  /**
   * Game data that is used to display a summary of what occurred in the game.
   */
  @Input() gameData: DrawGameDataModel[] = [] as DrawGameDataModel[];

  /**
   * Array of numbers used for the game summary.
   */
  public numbers: number[] = Array(this.sseService.gameDocument.totalHands).fill('').map((v, i) => i + 1);

  constructor(
    private gameService: GameService,
    private emittersService: EmittersService,
    private sseService: SseService) {
  }

  /**
   * Getter for the game model.
   */
  public get gameModel(): GameDocument {
    return this.sseService.gameDocument;
  }

  ngOnInit(): void {
    this.sseService.closeEvent(EmitterType.Hand);
    this.sseService.closeEvent(EmitterType.Game);
    this.sseService.closeEvent(EmitterType.GameData);
  }
}
