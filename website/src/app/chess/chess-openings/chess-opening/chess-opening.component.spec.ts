import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessOpeningComponent } from './chess-opening.component';

describe('ChessOpeningComponent', () => {
  let component: ChessOpeningComponent;
  let fixture: ComponentFixture<ChessOpeningComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessOpeningComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessOpeningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
