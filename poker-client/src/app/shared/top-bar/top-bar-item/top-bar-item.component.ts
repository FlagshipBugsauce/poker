/* tslint:disable */
import {Component, Input, OnInit} from '@angular/core';
import {DropDownMenuItem, MenuItem} from "../../models/menu-item.model";
import {Router} from "@angular/router";

@Component({
  selector: 'pkr-top-bar-item',
  templateUrl: './top-bar-item.component.html',
  styleUrls: ['./top-bar-item.component.scss']
})
export class TopBarItemComponent implements OnInit {
  @Input() menuItem: DropDownMenuItem;

  public get active(): boolean {
    return this.router.url === `/${this.menuItem.anchor}`;
  }

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

}
