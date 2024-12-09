import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChessPlayerAnalysisComponent} from './chess-player-analysis.component';

describe('ChessPlayerAnalysisComponent', () => {
  let component: ChessPlayerAnalysisComponent;
  let fixture: ComponentFixture<ChessPlayerAnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessPlayerAnalysisComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessPlayerAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
