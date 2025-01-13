import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChessUpdateOpeningComponent} from './chess-update-opening.component';

describe('ChessUpdateOpeningComponent', () => {
  let component: ChessUpdateOpeningComponent;
  let fixture: ComponentFixture<ChessUpdateOpeningComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessUpdateOpeningComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessUpdateOpeningComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
