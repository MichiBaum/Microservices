import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelectChessEventCategoryComponent} from './select-chess-event-category.component';

describe('SelectChessEventCategoryComponent', () => {
  let component: SelectChessEventCategoryComponent;
  let fixture: ComponentFixture<SelectChessEventCategoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectChessEventCategoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectChessEventCategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
