/* tslint:disable */
import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Store} from "@ngrx/store";
import {AppStateContainer, PokerTableStateContainer} from "../../../shared/models/app-state.model";
import {selectLoggedInUser, selectPlayers, selectPokerTable} from "../../../state/app.selector";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import {GamePlayer} from "../../../api/models/game-player";
import {ClientUser} from "../../../api/models/client-user";
import {PokerTable} from "../../../api/models/poker-table";
import {GameAction} from "../../../shared/models/game-action.enum";
import {performGameAction} from "../../../state/app.actions";

@Component({
  selector: 'pkr-table-controls',
  templateUrl: './table-controls.component.html',
  styleUrls: ['./table-controls.component.scss']
})
export class TableControlsComponent implements OnInit, OnDestroy {
  public raise: number = this.minRaise;
  public defaultSlider: SliderModel = {
    min: this.minRaise,
    max: this.bankRoll,
    step: this.minRaise / 10
  }
  @ViewChild('raiseInput') public raiseInput: ElementRef;

  public actions = GameAction;
  public players: GamePlayer[] = [];
  public player: GamePlayer;
  public user: ClientUser;
  public table: PokerTable;
  /**
   * Used to ensure we're not maintaining multiple subscriptions.
   */
  public ngDestroyed$ = new Subject<any>();

  constructor(
    private appStore: Store<AppStateContainer>,
    private pokerTableStore: Store<PokerTableStateContainer>
  ) {
  }

  public get safe(): boolean {
    return this.table && this.player && this.player.controls != null;
  }

  public get displayControls(): boolean {
    return this.safe ? this.acting && this.table.betting : false;
  }

  public get acting(): boolean {
    return this.safe ? this.players[this.table.actingPlayer].id === this.player.id : false;
  }

  public get toCall(): number {
    return this.safe ?
      this.player.controls.toCall > this.bankRoll ? this.bankRoll : this.player.controls.toCall : 0;
  }

  public get canCheck(): boolean {
    return this.toCall === 0;
  }

  public get minRaise(): number {
    return this.safe ? Math.min(this.table.minRaise, this.bankRoll - this.toCall) : 0;
  }

  public get canRaise(): boolean {
    return this.safe ? this.player.controls.toCall < this.bankRoll : false;
  }

  public get slider(): SliderModel {
    return this.safe ? {
      min: this.minRaise,
      max: this.bankRoll - this.toCall,
      step: 1
    } : this.defaultSlider;
  }

  public get bankRoll(): number {
    return this.safe ? this.player.controls.bankRoll : 0;
  }

  ngOnInit(): void {
    this.appStore.select(selectLoggedInUser)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((user: ClientUser) => this.user = user);

    this.pokerTableStore.select(selectPlayers)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((players: GamePlayer[]) => {
        // TODO: Should replace this with request for one players data.
        this.players = players;
        if (players && players.length > 0) {
          this.player = players.find(p => p.id === this.user.id);
        }
      });

    this.pokerTableStore.select(selectPokerTable)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((table: PokerTable) => {
        this.table = table;
        if (this.safe && this.table.players[this.table.actingPlayer].id === this.user.id) {
          this.raise = this.minRaise;
        }
      });
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  public raiseChanged($event): void {
    // const val = $event.target.value;
    // this.raise = val <= this.bankRoll ? val : this.bankRoll;
    // this.raise = this.raise >= Math.min(this.minRaise, this.bankRoll) ? this.raise :
    //   Math.min(this.minRaise, this.bankRoll);
    // this.raiseInput.nativeElement.value = this.raise;
    this.raise = $event.target.value;
  }

  public performAction(type: GameAction) {
    this.pokerTableStore.dispatch(performGameAction({
      actionType: type,
      playerId: this.player.id,
      raise: type === GameAction.Raise ? this.raise : null
    }));
  }

  private async delay(time: number): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, time));
  }
}

export interface SliderModel {
  min: number;
  max: number;
  step: number;
}
