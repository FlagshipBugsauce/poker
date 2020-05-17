import {Component, OnInit} from '@angular/core';
import {ApiConfiguration} from 'src/app/api/api-configuration';
import {Router} from '@angular/router';
import {EmittersService, GameService, HandService} from 'src/app/api/services';
import {SseService} from 'src/app/shared/sse.service';
import {ApiInterceptor} from 'src/app/api-interceptor.service';
import {EmitterType} from 'src/app/shared/models/emitter-type.model';
import {HandModel} from 'src/app/api/models/hand-model';
import {AuthService} from 'src/app/shared/auth.service';

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
    private emittersService: EmittersService,
    private handService: HandService,
    private sseService: SseService,
    private apiInterceptor: ApiInterceptor,
    private authService: AuthService) { }

  ngOnInit(): void {
    this.sseService
      .getServerSentEvent(
        `${this.apiConfiguration.rootUrl}/emitters/request/${EmitterType.Hand}/${this.apiInterceptor.jwt}`, EmitterType.Hand)
      .subscribe((event: any) => {
        try {
          this.hand = JSON.parse(event);
          this.canRoll = this.hand.playerToAct === this.authService.userModel.id;

          // Add the message to
          const lastAction = this.hand.actions != null ? this.hand.actions[this.hand.actions.length - 1] : null;
          if (lastAction != null) {
            this.actionText.push(lastAction.message);
          }

        } catch (err) {
          console.log('Something went wrong with the hand emitter.');
          this.sseService.closeEvent(EmitterType.Hand);
        }
      });

    this.refreshHand().then();
  }

  private async refreshHand(): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, 1200));
    this.emittersService.requestUpdate({ type: EmitterType.Hand }).subscribe(() => { });
  }

  public roll(): void {
    this.handService.roll().subscribe(() => { });
  }
}
