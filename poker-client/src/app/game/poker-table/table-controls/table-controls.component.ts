/* tslint:disable */
import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Store} from "@ngrx/store";
import {AppStateContainer, PokerTableStateContainer} from "../../../shared/models/app-state.model";
import {selectLoggedInUser, selectPlayers, selectPokerTable} from "../../../state/app.selector";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import {GamePlayerModel} from "../../../api/models/game-player-model";
import {ClientUserModel} from "../../../api/models/client-user-model";
import {PokerTableModel} from "../../../api/models/poker-table-model";

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

  public acting: boolean = true;
  public otherBet: number = 120;
  public currentBet: number = 40;
  public players: GamePlayerModel[] = [];
  public player: GamePlayerModel;
  public user: ClientUserModel;
  public table: PokerTableModel;
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
    return this.table && this.player && this.player.controls;
  }

  public get amountToCall(): number {
    return this.otherBet - this.currentBet;
  }

  public get toCall(): number {
    return this.safe ? this.player.controls.toCall : 0;
  }

  public get canCheck(): boolean {
    return this.toCall === 0;
  }

  public get slider(): SliderModel {
    return this.safe ? {
      min: this.table.minRaise,
      max: this.player.controls.bankRoll,
      step: 1
    } : this.defaultSlider;
  }

  public get bankRoll(): number {
    return this.safe ? this.player.controls.bankRoll : 420;
  }

  public get minRaise(): number {
    return this.safe ? this.table.minRaise : 420;
  }

  ngOnInit(): void {
    this.appStore.select(selectLoggedInUser)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((user: ClientUserModel) => this.user = user);

    this.pokerTableStore.select(selectPlayers)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((players: GamePlayerModel[]) => {
      this.players = players;
      if (players && players.length > 0) {
        console.log(this.user);
        this.player = players.find(p => p.id === this.user.id);
      }
    });

    this.pokerTableStore.select(selectPokerTable)
    .pipe(takeUntil(this.ngDestroyed$))
    .subscribe((table: PokerTableModel) => this.table = table);
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
    this.ngDestroyed$.complete();
  }

  public raiseChanged($event): void {
    const val = $event.target.value;
    this.raise = val <= this.bankRoll ? val : this.bankRoll;
    this.raise = this.raise >= this.minRaise ? this.raise : this.minRaise;
    this.raiseInput.nativeElement.value = this.raise;
  }

}

export interface SliderModel {
  min: number;
  max: number;
  step: number;
}
