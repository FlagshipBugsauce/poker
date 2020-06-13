import {TestBed} from '@angular/core/testing';

import {SseService} from './sse.service';
import {SharedModule} from './shared.module';

describe('SseService', () => {
  let service: SseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule]});
    service = TestBed.inject(SseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
