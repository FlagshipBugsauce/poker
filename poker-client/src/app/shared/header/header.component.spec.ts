import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HeaderComponent} from './header.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from '../shared.module';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {AuthService} from '../auth.service';
import {TopBarService} from '../top-bar/top-bar.service';
import {AppState} from '../models/app-state.model';

class MockAuthService {
  public authenticated: boolean = false;
}

class MockTopBarService {
  public authenticated: boolean = false;
}

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let store: MockStore;
  const initialState: AppState = {
    currentPage: '',
    authenticated: false,
    inLobby: false,
    inGame: false
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HeaderComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        provideMockStore(),
        {
          provider: TopBarService,
          useClass: MockTopBarService
        },
      ]
    })
      .compileComponents();
    store = TestBed.inject(MockStore);
    // store.setState(initialState);
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
