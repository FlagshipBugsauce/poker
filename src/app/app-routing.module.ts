import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home/home.component';
import {LoginComponent} from './shared/login/login.component';
import {AuthGuardService} from './shared/auth-guard.service';
import {RegisterComponent} from './shared/register/register.component';
import {CreateComponent} from './game/create/create.component';
import {JoinComponent} from './game/join/join.component';
import {GameComponent} from './game/game/game.component';
import {LeaveGameGuardService} from './game/game/leave-game-guard.service';
import {LeaveJoinPageGuardService} from './game/join/leave-join-page-guard.service';

const routes: Routes = [
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'home', component: HomeComponent},
  {path: 'create', component: CreateComponent, canActivate: [AuthGuardService]},
  {path: 'join', component: JoinComponent, canActivate: [AuthGuardService], canDeactivate: [LeaveJoinPageGuardService]},
  {path: 'game/:gameId', component: GameComponent, canActivate: [AuthGuardService], canDeactivate: [LeaveGameGuardService]},
  {path: '', redirectTo: 'home', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
