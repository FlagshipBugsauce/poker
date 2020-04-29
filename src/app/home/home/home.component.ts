import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/shared/auth.service';

@Component({
  selector: 'pkr-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  
  images: string[] = [
    "001", "002", "003", "004", "005", "006"
  ].map(n => `../../assets/images/poker${n}.png`);

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
    this.authService.authorize("admin@domain.com", "admin!@#");
  }

}
