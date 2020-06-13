import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PlayComponent} from './play.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from '../../shared/shared.module';
import {SseService} from '../../shared/sse.service';
import {GameDocument} from '../../api/models/game-document';
import {EmitterType} from '../../shared/models/emitter-type.model';

class MockSseService {
  public gameDocument: GameDocument = {
    players: []
  } as GameDocument;
  public closeEvent(type: EmitterType): void {}
  public addCallback(type: EmitterType, callback: () => void): void {}
  public openEvent(type: EmitterType, callback: () => void = null): void {}
}

describe('PlayComponent', () => {
  let component: PlayComponent;
  let fixture: ComponentFixture<PlayComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayComponent],
      providers: [{
        provide: SseService,
        useClass: MockSseService
      }],
      imports: [RouterTestingModule, SharedModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
