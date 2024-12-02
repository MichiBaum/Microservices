import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectChessPersonComponent } from './select-chess-person.component';

describe('SelectChessPersonComponent', () => {
  let component: SelectChessPersonComponent;
  let fixture: ComponentFixture<SelectChessPersonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectChessPersonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectChessPersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
