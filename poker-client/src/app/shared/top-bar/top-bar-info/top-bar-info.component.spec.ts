import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TopBarInfoComponent } from './top-bar-info.component';

describe('TopBarInfoComponent', () => {
  let component: TopBarInfoComponent;
  let fixture: ComponentFixture<TopBarInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TopBarInfoComponent ]
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
