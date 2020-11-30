import {TestBed} from '@angular/core/testing';

import {OpenRouteGuardService} from './open-route-guard.service';
import {provideMockStore} from '@ngrx/store/testing';
import {WebSocketService} from './web-socket/web-socket.service';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from './shared.module';

describe('OpenRouteGuardService', () => {
  let service: OpenRouteGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useValue: {}
        }
      ]
    });
    service = TestBed.inject(OpenRouteGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
