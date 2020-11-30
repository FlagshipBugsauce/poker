import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {CreateComponent} from './create.component';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';
import {provideMockStore} from '@ngrx/store/testing';
import {WebSocketService} from '../../shared/web-socket/web-socket.service';
import {CreateGameService} from '../../shared/web-socket/create-game.service';
import {MockCreateGameService} from '../../testing/mock-services';

describe('CreateComponent', () => {
  let component: CreateComponent;
  let fixture: ComponentFixture<CreateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [CreateComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        provideMockStore(),
        {
          provide: WebSocketService,
          useClass: jest.fn()
        },
        {
          provide: CreateGameService,
          useClass: MockCreateGameService
        }
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
