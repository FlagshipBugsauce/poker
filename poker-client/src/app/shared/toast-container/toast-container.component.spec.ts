import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {ToastContainerComponent} from './toast-container.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';

describe('ToastContainerComponent', () => {
  let component: ToastContainerComponent;
  let fixture: ComponentFixture<ToastContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ToastContainerComponent],
      imports: [
        NgbModule
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ToastContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
