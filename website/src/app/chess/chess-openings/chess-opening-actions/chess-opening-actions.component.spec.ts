import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessOpeningActionsComponent } from './chess-opening-actions.component';

describe('ChessOpeningActionsComponent', () => {
  let component: ChessOpeningActionsComponent;
  let fixture: ComponentFixture<ChessOpeningActionsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessOpeningActionsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessOpeningActionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
