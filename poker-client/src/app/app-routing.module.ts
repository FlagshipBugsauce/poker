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
import {APP_ROUTES} from './app-routes';
import {OpenRouteGuardService} from './shared/open-route-guard.service';
import {AboutComponent} from './shared/about/about.component';

const routes: Routes = [
  {path: 'logout', component: LoginComponent, canActivate: [AuthGuardService]},
  {path: APP_ROUTES.LOGIN.path, component: LoginComponent, canActivate: [OpenRouteGuardService]},
  {path: APP_ROUTES.ABOUT.path, component: AboutComponent, canActivate: [OpenRouteGuardService]},
  {
    path: APP_ROUTES.REGISTER.path,
    component: RegisterComponent,
    canActivate: [OpenRouteGuardService]
  },
  {path: APP_ROUTES.CREATE_GAME.path, component: CreateComponent, canActivate: [AuthGuardService]},
  {
    path: APP_ROUTES.JOIN_GAME.path,
    component: JoinComponent,
    canActivate: [AuthGuardService],
    canDeactivate: [LeaveJoinPageGuardService]
  },
  {
    path: `${APP_ROUTES.GAME_PREFIX.path}/:${APP_ROUTES.GAME_ID.path}`,
    component: GameComponent,
    canActivate: [AuthGuardService],
    canDeactivate: [LeaveGameGuardService]
  },
  {
    path: APP_ROUTES.HOME.path,
    component: HomeComponent,
    pathMatch: 'full',
    canActivate: [OpenRouteGuardService]
  },
  {path: '**', redirectTo: APP_ROUTES.HOME.path, canActivate: [OpenRouteGuardService]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
