/* tslint:disable */
import {CssPositionModel} from '../../../shared/models/css-position.model';

export class PositionHelperUtil {
  public static getIconBoxPosition(position: number): CssPositionModel {
    const iconBoxPosition: CssPositionModel = {top: NaN, left: NaN};
    switch (position) {
      case 0:
      case 1:
      case 9:
        iconBoxPosition.top = -40;
        break;
      case 2:
      case 3:
      case 7:
      case 8:
        iconBoxPosition.top = 60;
        break;
      case 4:
      case 5:
      case 6:
        iconBoxPosition.top = 165;
        break;
    }
    switch (position) {
      case 0:
      case 5:
        iconBoxPosition.left = 110;
        break;
      case 1:
      case 4:
        iconBoxPosition.left = 185;
        break;
      case 2:
      case 3:
        iconBoxPosition.left = 265;
        break;
      case 6:
      case 9:
        iconBoxPosition.left = 35;
        break;
      case 7:
      case 8:
        iconBoxPosition.left = -45;
        break;
    }
    return iconBoxPosition;
  }

  public static getAwayIconPosition(position: number): CssPositionModel {
    const awayIconPosition: CssPositionModel = {top: 0, left: 0};
    switch (position) {
      case 0:
      case 5:
        awayIconPosition.left = 33;
        break;
      case 1:
      case 4:
        awayIconPosition.left = -33;
        break;
      case 2:
      case 3:
        awayIconPosition.top = 33;
        break;
      case 6:
      case 9:
        awayIconPosition.left = 33;
        break;
      case 7:
      case 8:
        awayIconPosition.top = 33;
        break;
    }
    return awayIconPosition;
  }

  public static getChipsBoxPosition(position: number): CssPositionModel {
    const chipsBoxPosition: CssPositionModel = {top: 0, left: 0};
    switch (position) {
      case 0:
      case 1:
      case 9:
        chipsBoxPosition.top = -63;
        break;
      case 2:
      case 3:
      case 7:
      case 8:
        chipsBoxPosition.top = 80;
        break;
      case 4:
      case 5:
      case 6:
        chipsBoxPosition.top = 200;
        break;
    }
    switch (position) {
      case 0:
      case 5:
        chipsBoxPosition.left = 110;
        break;
      case 1:
      case 4:
        chipsBoxPosition.left = 185;
        break;
      case 2:
      case 3:
        chipsBoxPosition.left = 300;
        break;
      case 6:
      case 9:
        chipsBoxPosition.left = 35;
        break;
      case 7:
      case 8:
        chipsBoxPosition.left = -100;
        break;
    }
    return chipsBoxPosition;
  }
}
