import {async, ComponentFixture, TestBed} from '@angular/core/testing';
import {TopBarItemComponent} from './top-bar-item.component';
import {RouterTestingModule} from '@angular/router/testing';
import {SharedModule} from '../../shared.module';
import {DropDownMenuItem} from '../../models/menu-item.model';
import {Component} from '@angular/core';

describe('TopBarItemComponent', () => {
  const menuItem = {
    anchor: '',
    text: ''
  } as DropDownMenuItem;
  let component: TopBarItemComponentContainer;
  let fixture: ComponentFixture<TopBarItemComponentContainer>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TopBarItemComponent, TopBarItemComponentContainer],
      imports: [RouterTestingModule, SharedModule]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TopBarItemComponentContainer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  @Component({
    selector: 'pkr-top-bar-item-component-container',
    template: `<pkr-top-bar-item [menuItem]="{anchor: '', text: ''}"></pkr-top-bar-item>`
  })
  class TopBarItemComponentContainer {}
});
