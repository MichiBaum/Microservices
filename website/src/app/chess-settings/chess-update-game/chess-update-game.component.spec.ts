import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessUpdateGameComponent } from './chess-update-game.component';

describe('ChessUpdateGameComponent', () => {
  let component: ChessUpdateGameComponent;
  let fixture: ComponentFixture<ChessUpdateGameComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessUpdateGameComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessUpdateGameComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
