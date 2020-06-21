export interface MenuItem {
  /**
   * Text that will appear on the menu.
   */
  text: string;

  /**
   * Router link.
   */
  anchor: string;
}

export interface DropDownMenuItem extends MenuItem {
  /**
   * Optionally specify a list of MenuItems that will appear as a dropdown.
   */
  dropDown?: DropDownMenuItem[];
}
