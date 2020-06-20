import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TopBarItemComponent } from './top-bar-item.component';

describe('TopBarItemComponent', () => {
  let component: TopBarItemComponent;
  let fixture: ComponentFixture<TopBarItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TopBarItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TopBarItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
