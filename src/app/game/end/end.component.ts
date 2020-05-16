import { Component, OnInit, Input } from '@angular/core';
import { GameSummaryModel } from 'src/app/api/models';
import { GameService, EmittersService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { EmitterType } from 'src/app/shared/models/emitter-type.model';

@Component({
  selector: 'pkr-end',
  templateUrl: './end.component.html',
  styleUrls: ['./end.component.scss']
})
export class EndComponent implements OnInit {
  @Input('summary') summary: GameSummaryModel = <GameSummaryModel> { message: "" };

  constructor(
    private gameService: GameService, 
    private emittersService: EmittersService, 
    private sseService: SseService) { }

  ngOnInit(): void {
    this.sseService.closeEvent(EmitterType.Hand);
  }

}
