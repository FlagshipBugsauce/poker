import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {
  MiscEventsStateContainer,
  PokerTableStateContainer,
  PrivatePlayerDataStateContainer
} from '../../shared/models/app-state.model';

@Component({
  selector: 'pkr-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  images: string[] = [
    '001', '002', '003', '004', '005', '006'
  ].map(n => `assets/images/poker${n}.png`);

  constructor(
    private pokerTableStore: Store<PokerTableStateContainer>,
    private miscEventsStore: Store<MiscEventsStateContainer>,
    private privatePlayerDataStore: Store<PrivatePlayerDataStateContainer>) {
  }

  ngOnInit(): void {
    // // // TODO: This is for design purposes only.
    // this.delay(500).then(() => this.pokerTableStore.dispatch(pokerTableUpdate(table2)));
    // this.delay(500).then(() => this.miscEventsStore.dispatch(dealCards({id: '0', numCards: 69})));
    // this.delay(500).then(() =>
    //   this.privatePlayerDataStore.dispatch(privatePlayerDataUpdated(samplePlayer)));
  }

  private async delay(time: number): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, time));
  }
}
