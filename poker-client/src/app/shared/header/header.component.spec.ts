import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {HeaderComponent} from './header.component';
import {RouterTestingModule} from '@angular/router/testing';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {AppState} from '../models/app-state.model';
import {TopBarComponent} from '../top-bar/top-bar.component';
import {TopBarItemComponent} from '../top-bar/top-bar-item/top-bar-item.component';

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
      declarations: [
        HeaderComponent,
        TopBarComponent,
        TopBarItemComponent
      ],
      imports: [RouterTestingModule],
      providers: [
        provideMockStore(),
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
