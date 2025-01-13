import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessUpdateEngineComponent } from './chess-update-engine.component';

describe('ChessUpdateEngineComponent', () => {
  let component: ChessUpdateEngineComponent;
  let fixture: ComponentFixture<ChessUpdateEngineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessUpdateEngineComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessUpdateEngineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
