import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TopBarComponent} from './top-bar.component';
import {provideMockStore} from '@ngrx/store/testing';
import {TopBarItemComponent} from './top-bar-item/top-bar-item.component';
import {RouterTestingModule} from '@angular/router/testing';
import {TopBarInfoComponent} from './top-bar-info/top-bar-info.component';

describe('TopBarComponent', () => {
  let component: TopBarComponent;
  let fixture: ComponentFixture<TopBarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TopBarComponent,
        TopBarItemComponent,
        TopBarInfoComponent
      ],
      imports: [
        RouterTestingModule
      ],
      providers: [
        provideMockStore()
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TopBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
