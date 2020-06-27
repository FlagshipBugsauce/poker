import {TestBed} from '@angular/core/testing';
import {LeaveJoinPageGuardService} from './leave-join-page-guard.service';
import {SharedModule} from '../../shared/shared.module';
import {provideMockStore} from '@ngrx/store/testing';

describe('LeaveJoinPageGuardService', () => {
  let service: LeaveJoinPageGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [provideMockStore()]
    });
    service = TestBed.inject(LeaveJoinPageGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
