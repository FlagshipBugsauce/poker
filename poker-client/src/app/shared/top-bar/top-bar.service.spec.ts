import { TestBed } from '@angular/core/testing';
import { TopBarService } from './top-bar.service';
import {MockStore, provideMockStore} from '@ngrx/store/testing';
import {SharedModule} from '../shared.module';

describe('TopBarService', () => {
  const initialState = {
    currentPage: '',
    authenticated: false,
    inLobby: false,
    inGame: false
  };
  let store: MockStore;
  let service: TopBarService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [
        provideMockStore({initialState})
      ]
    });
    service = TestBed.inject(TopBarService);
    store = TestBed.inject(MockStore);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
