import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'pkr-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  images: string[] = [
    '001', '002', '003', '004', '005', '006'
  ].map(n => `assets/images/poker${n}.png`);

  constructor() {}

  ngOnInit(): void {
  }
}
