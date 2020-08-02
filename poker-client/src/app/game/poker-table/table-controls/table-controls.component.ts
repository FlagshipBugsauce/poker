/* tslint:disable */
import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';

@Component({
  selector: 'pkr-table-controls',
  templateUrl: './table-controls.component.html',
  styleUrls: ['./table-controls.component.scss']
})
export class TableControlsComponent implements OnInit {
  public minRaise: number = 20;
  public raise: number = this.minRaise;
  public bankRoll: number = 420;
  public slider: SliderModel = {
    min: this.minRaise,
    max: this.bankRoll,
    step: this.minRaise / 10
  }
  @ViewChild('raiseInput') public raiseInput: ElementRef;

  public acting: boolean = true;
  public otherBet: number = 120;
  public currentBet: number = 40;

  constructor() {
  }

  public get amountToCall(): number {
    return this.otherBet - this.currentBet;
  }

  ngOnInit(): void {
    console.log(this.slider);
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
