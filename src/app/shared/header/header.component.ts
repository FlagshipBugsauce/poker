import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'pkr-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  icon: string = "../../assets/icons/aces.svg";

  constructor(public router: Router, public authService: AuthService) { }

  ngOnInit(): void {
  }
}
