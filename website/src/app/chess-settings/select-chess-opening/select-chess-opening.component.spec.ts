import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectChessOpeningComponent } from './select-chess-opening.component';

describe('SelectChessOpeningComponent', () => {
  let component: SelectChessOpeningComponent;
  let fixture: ComponentFixture<SelectChessOpeningComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectChessOpeningComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectChessOpeningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
