import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {GameComponent} from './game.component';
import {SharedModule} from '../../shared/shared.module';
import {RouterTestingModule} from '@angular/router/testing';
import {LobbyComponent} from '../lobby/lobby.component';
import {PlayComponent} from '../play/play.component';
import {EndComponent} from '../end/end.component';

describe('GameComponent', () => {
  let component: GameComponent;
  let fixture: ComponentFixture<GameComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        GameComponent,
        LobbyComponent,
        PlayComponent,
        EndComponent
      ],
      imports: [SharedModule, RouterTestingModule]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
