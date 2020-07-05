/* tslint:disable */
import {ModuleWithProviders, NgModule, Optional, SkipSelf} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ApiConfiguration, ApiConfigurationParams} from './api-configuration';

import {GameService} from './services/game.service';
import {HandService} from './services/hand.service';
import {TestControllerService} from './services/test-controller.service';
import {UsersService} from './services/users.service';

/**
 * Module that provides all services and configuration.
 */
@NgModule({
  imports: [],
  exports: [],
  declarations: [],
  providers: [
    GameService,
    HandService,
    TestControllerService,
    UsersService,
    ApiConfiguration
  ],
})
export class ApiModule {
  constructor(
    @Optional() @SkipSelf() parentModule: ApiModule,
    @Optional() http: HttpClient
  ) {
    if (parentModule) {
      throw new Error('ApiModule is already loaded. Import in your base AppModule only.');
    }
    if (!http) {
      throw new Error('You need to import the HttpClientModule in your AppModule! \n' +
        'See also https://github.com/angular/angular/issues/20575');
    }
  }

  static forRoot(params: ApiConfigurationParams): ModuleWithProviders<ApiModule> {
    return {
      ngModule: ApiModule,
      providers: [
        {
          provide: ApiConfiguration,
          useValue: params
        }
      ]
    }
  }
}
