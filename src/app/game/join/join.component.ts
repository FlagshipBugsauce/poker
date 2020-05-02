import { Component, OnInit, ViewChild } from '@angular/core';
import { PopupComponent, PopupContentModel } from 'src/app/shared/popup/popup.component';

@Component({
  selector: 'pkr-join',
  templateUrl: './join.component.html',
  styleUrls: ['./join.component.scss']
})
export class JoinComponent implements OnInit {
  // TODO: Remove when backend can return games.
  private _games = sampleGames;

  // Pagination fields:
  public page: number = 1;
  public pageSize: number = 5;
  public totalGames: number = this._games.length;

  @ViewChild('popup') public confirmationPopup: PopupComponent;
  public popupContent: PopupContentModel[] = <PopupContentModel[]> [
    <PopupContentModel> { body: "" },
    <PopupContentModel> { body: "Click cancel if you do not wish to proceed." }
  ];
  public popupOkCloseProcedure: Function = () => { };

  /** Returns a slice of the list of games for pagination. */
  public get games(): any[] {
    return sampleGames
      .map((game: any) => ({...game}))
      .slice((this.page - 1) * this.pageSize, (this.page - 1) * this.pageSize + this.pageSize);
  }

  constructor() { }

  ngOnInit(): void {
  }

  public showConfirmationPopup(game: any): void {
    this.popupContent[0].body = `Attempting to join game "${game.name}".`;
    this.popupOkCloseProcedure = () => {
      // TODO: Join game lobby.
      console.log(game);
    }
    this.confirmationPopup.open();
  }
}

// Sample data for table.
export const sampleGames: any[] = [
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Poker with Randy McGee",
    host: "Randy McGee",
    currentPlayers: 2,
    maxPlayers: 6,
    buyIn: 69
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Justin's Summer Sunday Smokefest",
    host: "Justin Stephenson",
    currentPlayers: 6,
    maxPlayers: 9,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "big shots poker",
    host: "Billy Bob McGraw",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "YorkU Poker Tournament",
    host: "Prof. Jeff Edmonds",
    currentPlayers: 1,
    maxPlayers: 10,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Thinking Inductively with A. Mirzaian",
    host: "Andranik Mirzaian",
    currentPlayers: 5,
    maxPlayers: 6,
    buyIn: 69
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "design by contract",
    host: "Jackie Wang DPhil, Oxon",
    currentPlayers: 6,
    maxPlayers: 9,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Poker with Randy McGee",
    host: "Randy McGee",
    currentPlayers: 2,
    maxPlayers: 6,
    buyIn: 69
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Justin's Summer Sunday Smokefest",
    host: "Justin Stephenson",
    currentPlayers: 6,
    maxPlayers: 9,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "big shots poker",
    host: "Billy Bob McGraw",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "YorkU Poker Tournament",
    host: "Prof. Jeff Edmonds",
    currentPlayers: 1,
    maxPlayers: 10,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Thinking Inductively with A. Mirzaian",
    host: "Andranik Mirzaian",
    currentPlayers: 5,
    maxPlayers: 6,
    buyIn: 69
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "design by contract",
    host: "Jackie Wang DPhil, Oxon",
    currentPlayers: 6,
    maxPlayers: 9,
    buyIn: 420
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  },
  {
    id: "0a7d95ef-94ba-47bc-b591-febb365bc543",
    name: "Jimmy's Friday Night Fun Time",
    host: "Jimmy McGillicutty",
    currentPlayers: 7,
    maxPlayers: 10,
    buyIn: 25
  }
]
