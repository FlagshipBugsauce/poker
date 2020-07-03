import {TestBed} from '@angular/core/testing';
import {LeaveGameGuardService} from './leave-game-guard.service';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';
import {provideMockStore} from '@ngrx/store/testing';
import {WebSocketService} from '../../shared/web-socket.service';

describe('LeaveGameGuardService', () => {
  let service: LeaveGameGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useClass: jest.fn()
        }
      ]
    });
    service = TestBed.inject(LeaveGameGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
