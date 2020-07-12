import {TestBed} from '@angular/core/testing';

import {OpenRouteGuardService} from './open-route-guard.service';

describe('OpenRouteGuardService', () => {
  let service: OpenRouteGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OpenRouteGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
