import { TestBed } from '@angular/core/testing';

import { LeaveJoinPageGuardService } from './leave-join-page-guard.service';

describe('LeaveJoinPageGuardService', () => {
  let service: LeaveJoinPageGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LeaveJoinPageGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
