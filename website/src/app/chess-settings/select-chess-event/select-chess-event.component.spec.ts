import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectChessEventComponent } from './select-chess-event.component';

describe('SelectChessEventComponent', () => {
  let component: SelectChessEventComponent;
  let fixture: ComponentFixture<SelectChessEventComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectChessEventComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectChessEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
