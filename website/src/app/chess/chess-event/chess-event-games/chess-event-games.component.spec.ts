import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChessEventGamesComponent} from './chess-event-games.component';

describe('ChessEventGamesComponent', () => {
  let component: ChessEventGamesComponent;
  let fixture: ComponentFixture<ChessEventGamesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessEventGamesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessEventGamesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
