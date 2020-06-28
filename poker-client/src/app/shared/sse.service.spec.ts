import {TestBed} from '@angular/core/testing';
import {SseService} from './sse.service';
import {SharedModule} from './shared.module';
import {provideMockStore} from '@ngrx/store/testing';

describe('SseService', () => {
  let service: SseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [provideMockStore()]
    });
    service = TestBed.inject(SseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
