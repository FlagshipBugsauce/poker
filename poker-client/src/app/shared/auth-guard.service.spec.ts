import {TestBed} from '@angular/core/testing';

import {AuthGuardService} from './auth-guard.service';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from './shared.module';

describe('AuthGuardService', () => {
  let service: AuthGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      // providers: [ApiInterceptor],
      imports: [RouterTestingModule, SharedModule]
    });
    service = TestBed.inject(AuthGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
