import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TopBarInfoComponent} from './top-bar-info.component';
import {provideMockStore} from '@ngrx/store/testing';

describe('TopBarInfoComponent', () => {
  let component: TopBarInfoComponent;
  let fixture: ComponentFixture<TopBarInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TopBarInfoComponent],
      providers: [provideMockStore()]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TopBarInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
