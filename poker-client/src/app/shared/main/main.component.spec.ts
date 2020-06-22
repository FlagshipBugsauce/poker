import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {MainComponent} from './main.component';
import {HeaderComponent} from '../header/header.component';
import {FooterComponent} from '../footer/footer.component';
import {RouterTestingModule} from '@angular/router/testing';
import {ToastContainerComponent} from '../toast-container/toast-container.component';
import {TopBarComponent} from '../top-bar/top-bar.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {TopBarItemComponent} from '../top-bar/top-bar-item/top-bar-item.component';
import {provideMockStore} from '@ngrx/store/testing';

describe('MainComponent', () => {
  const initialState = {
    currentPage: '',
    authenticated: false,
    inLobby: false,
    inGame: false
  };
  let component: MainComponent;
  let fixture: ComponentFixture<MainComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        MainComponent,
        HeaderComponent,
        FooterComponent,
        ToastContainerComponent,
        TopBarComponent,
        TopBarItemComponent
      ],
      imports: [
        RouterTestingModule,
        NgbModule
      ],
      providers: [
        provideMockStore({initialState})
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
