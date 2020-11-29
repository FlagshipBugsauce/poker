import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PopupAfkComponent} from './popup-afk.component';
import {provideMockStore} from '@ngrx/store/testing';

describe('PopupAfkComponent', () => {
  let component: PopupAfkComponent;
  let fixture: ComponentFixture<PopupAfkComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PopupAfkComponent],
      providers: [
        provideMockStore()
      ]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PopupAfkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
