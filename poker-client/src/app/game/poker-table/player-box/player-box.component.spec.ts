import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {PlayerBoxComponent} from './player-box.component';
import {SharedModule} from '../../../shared/shared.module';
import {provideMockStore} from '@ngrx/store/testing';

describe('PlayerBoxComponent', () => {
  let component: PlayerBoxComponent;
  let fixture: ComponentFixture<PlayerBoxComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PlayerBoxComponent],
      providers: [
        provideMockStore()
      ],
      imports: [
        SharedModule
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
