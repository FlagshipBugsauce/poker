import {TestBed} from '@angular/core/testing';

import {OpenRouteGuardService} from './open-route-guard.service';
import {provideMockStore} from '@ngrx/store/testing';

describe('OpenRouteGuardService', () => {
  let service: OpenRouteGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideMockStore()
      ]
    });
    service = TestBed.inject(OpenRouteGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
