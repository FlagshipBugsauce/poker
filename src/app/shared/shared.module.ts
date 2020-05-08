import { NgModule, Provider, forwardRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ApiInterceptor } from '../api-interceptor.service';
import { MainComponent } from './main/main.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { RouterModule } from '@angular/router';
import { UsersService } from '../api/services';
import { ApiModule } from '../api/api.module';
import { LoginComponent } from './login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RegisterComponent } from './register/register.component';
import { PopupComponent } from './popup/popup.component';
import { ToastContainerComponent } from './toast-container/toast-container.component';

export const API_INTERCEPTOR_PROVIDER: Provider = {
  provide: HTTP_INTERCEPTORS,
  useExisting: forwardRef(() => ApiInterceptor),
  multi: true
}

@NgModule({
  declarations: [
    MainComponent, 
    HeaderComponent, 
    FooterComponent, 
    LoginComponent, 
    RegisterComponent, 
    PopupComponent, 
    ToastContainerComponent
  ],
  imports: [
    CommonModule,
    NgbModule,
    ApiModule,
    RouterModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule
  ],
  providers: [
    UsersService,
    ApiInterceptor,
    API_INTERCEPTOR_PROVIDER
  ],
  exports: [
    NgbModule,
    ReactiveFormsModule,
    FormsModule,
    PopupComponent,
    RouterModule
  ]
})
export class SharedModule { }
