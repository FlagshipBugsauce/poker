import {TestBed} from '@angular/core/testing';

import {LeaveGameGuardService} from './leave-game-guard.service';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';

describe('LeaveGameGuardService', () => {
  let service: LeaveGameGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule]});
    service = TestBed.inject(LeaveGameGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
