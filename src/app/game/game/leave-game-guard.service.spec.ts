import { TestBed } from '@angular/core/testing';

import { LeaveGameGuardService } from './leave-game-guard.service';

describe('LeaveGameGuardService', () => {
  let service: LeaveGameGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeaveGameGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
