import {TestBed} from '@angular/core/testing';

import {AuthGuardService} from './auth-guard.service';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from './shared.module';
import {provideMockStore} from '@ngrx/store/testing';
import {WebSocketService} from './web-socket/web-socket.service';

describe('AuthGuardService', () => {
  let service: AuthGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      // providers: [ApiInterceptor],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useValue: {}
        }
      ]
    });
    service = TestBed.inject(AuthGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
