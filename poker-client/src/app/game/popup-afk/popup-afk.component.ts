import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {NgbActiveModal, NgbModal, NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {GameStateContainer, PlayerDataStateContainer} from '../../shared/models/app-state.model';
import {Store} from '@ngrx/store';
import {setAwayStatus} from '../../state/app.actions';
import {Subject} from 'rxjs';
import {selectGameModel} from '../../state/app.selector';
import {takeUntil} from 'rxjs/operators';
import {Game} from '../../api/models/game';

@Component({
  selector: 'pkr-popup-afk',
  templateUrl: './popup-afk.component.html',
  styleUrls: ['./popup-afk.component.scss']
})
export class PopupAfkComponent implements OnInit, OnDestroy {
  @ViewChild('popup') popup: NgbActiveModal;
  public ngDestroyed$ = new Subject();
  private ngbModalRef: NgbModalRef;
  private gameModel: Game;

  constructor(
    private ngbModal: NgbModal,
    private playerDataStore: Store<PlayerDataStateContainer>,
    private gameStore: Store<GameStateContainer>) {
  }

  ngOnInit(): void {
    this.gameStore.select(selectGameModel)
      .pipe(takeUntil(this.ngDestroyed$))
      .subscribe((data: Game) => this.gameModel = data);
  }

  public ngOnDestroy() {
    this.ngDestroyed$.next();
  }

  public open(): void {
    this.ngbModalRef = this.ngbModal.open(this.popup, {
      backdrop: 'static',
      centered: true,
      keyboard: false
    });
  }

  public back(): void {
    if (this.gameModel.phase === 'Play') {
      this.playerDataStore.dispatch(setAwayStatus({away: false}));
    }
    this.ngbModalRef.close();
  }
}
