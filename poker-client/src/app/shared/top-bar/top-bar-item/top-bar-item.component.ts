import {Component, Input, OnInit} from '@angular/core';
import {DropDownMenuItem} from '../../models/menu-item.model';
import {Router} from '@angular/router';

@Component({
  selector: 'pkr-top-bar-item',
  templateUrl: './top-bar-item.component.html',
  styleUrls: ['./top-bar-item.component.scss']
})
export class TopBarItemComponent implements OnInit {
  /**
   * Menu item that will be displayed.
   */
  @Input() menuItem: DropDownMenuItem;

  constructor(private router: Router) {
  }

  /**
   * Getter for a flag that will be used to determine whether the text in this menu item should be
   * highlighted to indicate that the user is currently on the page this item links to.
   */
  public get active(): boolean {
    const dropDownItems: DropDownMenuItem[] = this.menuItem.dropDown;
    let activeDropDown: boolean = false;
    if (dropDownItems) {
      activeDropDown = dropDownItems.find(item => this.router.url === `/${item.anchor}`) != null;
    }
    return this.router.url === `/${this.menuItem.anchor}` || activeDropDown;
  }

  ngOnInit(): void {
  }

  public activeDropdown(anchor: string) {
    return this.router.url === `/${anchor}`;
  }
}
