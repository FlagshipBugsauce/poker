import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastService } from 'src/app/shared/toast.service';
import { GameService } from 'src/app/api/services';
import { GetGameModel, CreateGameModel, ApiSuccessModel } from 'src/app/api/models';

@Component({
  selector: 'pkr-create',
  templateUrl: './create.component.html',
  styleUrls: ['./create.component.scss']
})
export class CreateComponent implements OnInit {
  public createGameForm: FormGroup;
  public maxPlayers: number = 10; // TODO: Retrieve this from the backend.

  constructor(
    private router: Router, 
    private formBuilder: FormBuilder, 
    public toastService: ToastService,
    private gameService: GameService) { }

  public ngOnInit(): void {
    this.createGameForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      maxPlayers: [2, [Validators.required]],
      buyIn: ['', [Validators.required]],
      // roundTime: ['', [Validators.required]],
    });
  }

  /**
   * 
   */
  public fillNumbers(): number[] {
    return Array(this.maxPlayers - 1).fill(this.maxPlayers).map((x, i) => i + 2);
  }

  public createGame(values: any): void {
    this.gameService.createGame({
      body: <CreateGameModel> {
        buyIn: values.buyIn,
        maxPlayers: values.maxPlayers,
        name: values.name
      }
    }).subscribe((response: ApiSuccessModel) => {
      this.router.navigate([`/game/${response.message}`]);
    });
  }
}
