import {TestBed} from '@angular/core/testing';
import {LeaveJoinPageGuardService} from './leave-join-page-guard.service';
import {SharedModule} from '../../shared/shared.module';
import {provideMockStore} from '@ngrx/store/testing';
import {WebSocketService} from '../../shared/web-socket.service';

describe('LeaveJoinPageGuardService', () => {
  let service: LeaveJoinPageGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useClass: jest.fn()
        }
      ]
    });
    service = TestBed.inject(LeaveJoinPageGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
