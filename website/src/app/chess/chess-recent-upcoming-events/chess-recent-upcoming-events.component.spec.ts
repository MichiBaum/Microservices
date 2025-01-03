import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChessRecentUpcomingEventsComponent} from './chess-recent-upcoming-events.component';

describe('ChessEventsRecentUpcomingComponent', () => {
  let component: ChessRecentUpcomingEventsComponent;
  let fixture: ComponentFixture<ChessRecentUpcomingEventsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessRecentUpcomingEventsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessRecentUpcomingEventsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
