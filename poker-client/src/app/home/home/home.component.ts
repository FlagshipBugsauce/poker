import {Component, OnInit} from '@angular/core';
import {Store} from '@ngrx/store';
import {PokerTableStateContainer} from '../../shared/models/app-state.model';
import {table} from '../../game/poker-table/sample-table';
import {pokerTableUpdate} from '../../state/app.actions';

@Component({
  selector: 'pkr-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  images: string[] = [
    '001', '002', '003', '004', '005', '006'
  ].map(n => `assets/images/poker${n}.png`);

  constructor(private pokerTableStore: Store<PokerTableStateContainer>) {
  }

  ngOnInit(): void {
    // TODO: This is for design purposes only.
    this.pokerTableStore.dispatch(pokerTableUpdate(table));
  }
}
