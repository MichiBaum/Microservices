import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessOpeningsComponent } from './chess-openings.component';

describe('ChessOpeningsComponent', () => {
  let component: ChessOpeningsComponent;
  let fixture: ComponentFixture<ChessOpeningsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessOpeningsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessOpeningsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
