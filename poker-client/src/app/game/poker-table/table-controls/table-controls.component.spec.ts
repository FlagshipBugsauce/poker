import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {TableControlsComponent} from './table-controls.component';
import {provideMockStore} from '@ngrx/store/testing';

describe('TableControlsComponent', () => {
  let component: TableControlsComponent;
  let fixture: ComponentFixture<TableControlsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TableControlsComponent],
      providers: [
        provideMockStore()
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TableControlsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
