import { Component, OnInit } from '@angular/core';
import { ApiConfiguration } from 'src/app/api/api-configuration';
import { Router } from '@angular/router';
import { GameService, HandService } from 'src/app/api/services';
import { SseService } from 'src/app/shared/sse.service';
import { ApiInterceptor } from 'src/app/api-interceptor.service';
import { EmitterType } from 'src/app/shared/models/emitter-type.model';
import { HandModel } from 'src/app/api/models/hand-model';
import { ApiSuccessModel } from 'src/app/api/models';
import { AuthService } from 'src/app/shared/auth.service';

@Component({
  selector: 'pkr-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.scss']
})
export class PlayComponent implements OnInit {
  public hand: HandModel;
  public canRoll: boolean = false;
  public actionText: string[] = [];

  constructor(
    private apiConfiguration: ApiConfiguration,
    private router: Router,
    private gameService: GameService,
    private handService: HandService,
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor,
    private authService: AuthService) { }

  ngOnInit(): void {
    this.sseService
      .getServerSentEvent(`${this.apiConfiguration.rootUrl}/game/emitter/hand/${this.apiInterceptor.jwt}`, EmitterType.Hand)
      .subscribe((event: any) => {
        try {
          this.hand = JSON.parse(event);
          this.canRoll = this.hand.playerToAct == this.authService.userModel.id;
          console.log(this.hand);

          // Add the message to
          let lastAction = this.hand.actions != null ? this.hand.actions[this.hand.actions.length - 1] : null;
          if (lastAction != null) {
            this.actionText.push(lastAction.message)
          }

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

  public roll(): void {
    this.handService.roll({ Authorization: null }).subscribe(() => { });
  }
}
