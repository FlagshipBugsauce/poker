/* tslint:disable */
import {CardModel} from './card-model';

export interface DeckModel {
  cards?: Array<CardModel>;
  usedCards?: Array<CardModel>;
}
