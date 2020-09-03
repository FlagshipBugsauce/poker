import {async, ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';
import {TopBarItemComponent} from './top-bar-item.component';
import {RouterTestingModule} from '@angular/router/testing';
import {DropDownMenuItem} from '../../models/menu-item.model';
import {Router} from '@angular/router';
import {By} from '@angular/platform-browser';

describe('TopBarItemComponent', () => {
  const basicMenuItem = {
    anchor: 'test-anchor',
    text: 'Test Text'
  } as DropDownMenuItem;
  const complexMenuItem = {
    anchor: 'main-anchor',
    text: 'Main Text',
    dropDown: [
      {
        anchor: 'test-anchor1',
        text: 'Test Text 1'
      },
      {
        anchor: 'test-anchor2',
        text: 'Test Text 2'
      },
      {
        anchor: 'test-anchor3',
        text: 'Test Text 3'
      }
    ]
  };
  let component: TopBarItemComponent;
  let fixture: ComponentFixture<TopBarItemComponent>;
  let router: Router;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [TopBarItemComponent],
      imports: [
        RouterTestingModule.withRoutes([
          {path: '', component: TopBarItemComponent},
          {path: 'main-anchor', component: TopBarItemComponent},
          {path: 'test-anchor', component: TopBarItemComponent},
          {path: 'test-anchor1', component: TopBarItemComponent},
          {path: 'test-anchor2', component: TopBarItemComponent},
          {path: 'test-anchor3', component: TopBarItemComponent}
        ])
      ]
    }).compileComponents();
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(TopBarItemComponent);
    component = fixture.componentInstance;
    component.menuItem = basicMenuItem;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not have dropdown', () => {
    const selection1 = fixture.debugElement.query(By.css('.top-bar-item'));
    const selection2 = fixture.debugElement.query(By.css('.text-light'));
    expect(selection1).toBeTruthy();
    expect(selection2).toBeTruthy();
    expect(selection1).toEqual(selection2);
    expect(selection1.name).toBe('span');
    expect(selection1.nativeElement.childNodes[0].innerHTML).toBe(basicMenuItem.text);
    expect(fixture.debugElement.query(By.css('.btn')).properties.href).toBe(`/${basicMenuItem.anchor}`);
  });

  it('should not be active when route doesn\'t match anchor', () => {
    const selection = fixture.debugElement.query(By.css('.top-bar-button-active'));
    expect(selection).toBeNull();
    expect(component.active).toBeFalsy();
  });

  it('should be active when route matches anchor', fakeAsync(() => {
    router.navigate(['test-anchor']);
    tick();
    fixture.detectChanges();
    const selection = fixture.debugElement.query(By.css('.top-bar-button-active'));
    expect(selection).toBeTruthy();
    expect(component.active).toBeTruthy();
  }));

  it('should have dropdown items', () => {
    component.menuItem = complexMenuItem;
    fixture.detectChanges();
    const selection = fixture.debugElement.query(By.css('.top-bar-item'));
    expect(selection).toBeTruthy();
    expect(selection.name).toBe('div');
    const dropDownItems = fixture.debugElement.queryAll(By.css('.top-bar-dropdown-button'));
    expect(dropDownItems).toBeTruthy();
    expect(dropDownItems.length).toBe(3);
    expect(dropDownItems[0].properties.href).toBe(`/${complexMenuItem.dropDown[0].anchor}`);
    expect(dropDownItems[1].properties.href).toBe(`/${complexMenuItem.dropDown[1].anchor}`);
    expect(dropDownItems[2].properties.href).toBe(`/${complexMenuItem.dropDown[2].anchor}`);
  });

  it('should not be active when route doesn\'t match any anchors', fakeAsync(() => {
    component.menuItem = complexMenuItem;
    router.navigate(['']);
    tick();
    fixture.detectChanges();
    const selection = fixture.debugElement.query(By.css('.top-bar-button-active'));
    const selections = fixture.debugElement.queryAll(By.css('.top-bar-dropdown-button-active'));
    expect(selection).toBeNull();
    expect(selections).toEqual([]);
    expect(component.active).toBeFalsy();
    expect(component.activeDropdown(complexMenuItem.dropDown[0].anchor)).toBeFalsy();
    expect(component.activeDropdown(complexMenuItem.dropDown[1].anchor)).toBeFalsy();
    expect(component.activeDropdown(complexMenuItem.dropDown[2].anchor)).toBeFalsy();
  }));

  it('only mainly anchor should be active when route matches main anchor', fakeAsync(() => {
    component.menuItem = complexMenuItem;
    router.navigate(['main-anchor']);
    tick();
    fixture.detectChanges();
    const selection = fixture.debugElement.query(By.css('.top-bar-button-active'));
    const selections = fixture.debugElement.queryAll(By.css('.top-bar-dropdown-button-active'));
    expect(selection).toBeTruthy();
    expect(selections).toEqual([]);
    expect(selection.name).toBe('a');
    expect(component.activeDropdown(complexMenuItem.dropDown[0].anchor)).toBeFalsy();
    expect(component.activeDropdown(complexMenuItem.dropDown[1].anchor)).toBeFalsy();
    expect(component.activeDropdown(complexMenuItem.dropDown[2].anchor)).toBeFalsy();
  }));

  it('should be active when route matches a dropdown anchor', fakeAsync(() => {
    component.menuItem = complexMenuItem;
    router.navigate(['test-anchor2']);
    tick();
    fixture.detectChanges();
    const selection = fixture.debugElement.query(By.css('.top-bar-button-active'));
    const selections = fixture.debugElement.queryAll(By.css('.top-bar-dropdown-button-active'));
    expect(selection).toBeTruthy();
    expect(selections.length).toBe(1);
    expect(selection.name).toBe('a');
    expect(component.activeDropdown(complexMenuItem.dropDown[0].anchor)).toBeFalsy();
    expect(component.activeDropdown(complexMenuItem.dropDown[1].anchor)).toBeTruthy();
    expect(component.activeDropdown(complexMenuItem.dropDown[2].anchor)).toBeFalsy();
    expect(selections[0].properties.href).toBe(`/${complexMenuItem.dropDown[1].anchor}`);
  }));
});
