/* Modules */
import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

/* Components */
import { MainComponent } from './main/main.component';
import { HeaderComponent } from './main/header/header.component';
import { FooterComponent } from './main/footer/footer.component';
import { HomeComponent } from './home/home.component';

/* Pipes */
/* Services */

@NgModule({
  declarations: [
    MainComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgbModule
  ],
  providers: [],
  bootstrap: [MainComponent]
})
export class AppModule { }
