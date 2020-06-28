import {TestBed} from '@angular/core/testing';
import {LeaveGameGuardService} from './leave-game-guard.service';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';
import {provideMockStore} from '@ngrx/store/testing';

describe('LeaveGameGuardService', () => {
  let service: LeaveGameGuardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [provideMockStore()]
    });
    service = TestBed.inject(LeaveGameGuardService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
