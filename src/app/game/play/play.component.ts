import { Component, OnInit } from '@angular/core';
import { ApiConfiguration } from 'src/app/api/api-configuration';
import { Router } from '@angular/router';
import { GameService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { ApiInterceptor } from 'src/app/api-interceptor.service';
import { EmitterType } from 'src/app/shared/models/emitter-type.model';
import { HandModel } from 'src/app/api/models/hand-model';
import { ApiSuccessModel } from 'src/app/api/models';

@Component({
  selector: 'pkr-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.scss']
})
export class PlayComponent implements OnInit {
  public hand: HandModel;

  constructor(
    private apiConfiguration: ApiConfiguration,
    private router: Router,
    private gameService: GameService,
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor) { }

  ngOnInit(): void {
    this.sseService
      .getServerSentEvent(`${this.apiConfiguration.rootUrl}/game/emitter/hand/${this.apiInterceptor.jwt}`, EmitterType.Hand)
      .subscribe((event: any) => {
        try {
          this.hand = JSON.parse(event);
        } catch(err) {
          console.log("Something went wrong with the hand emitter.");
          this.sseService.closeEvent(EmitterType.Hand);
        }
      });

    this.refreshHand();
  }

  private async refreshHand(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 100));
    this.gameService.refreshHand({ Authorization: null }).subscribe((response: ApiSuccessModel) => { });
  }
}
