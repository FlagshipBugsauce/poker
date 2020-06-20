/* tslint:disable */
import { Component, OnInit } from '@angular/core';
import {TopBarService} from "./top-bar.service";

@Component({
  selector: 'pkr-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss']
})
export class TopBarComponent implements OnInit {

  public get icon(): string {
    return this.topBarService.topBarIcon;
  }

  constructor(public topBarService: TopBarService) { }

  ngOnInit(): void {
  }

}
