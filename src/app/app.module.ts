/* Modules */
import { BrowserModule } from '@angular/platform-browser';
import { SharedModule } from './shared/shared.module';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';

/* Components */
import { MainComponent } from './shared/main/main.component';
import { HomeModule } from './home/home.module';

/* Pipes */
/* Services */

@NgModule({
  declarations: [
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    SharedModule,
    HomeModule
  ],
  exports: [
    SharedModule
  ],
  providers: [
  ],
  bootstrap: [MainComponent]
})
export class AppModule { }
