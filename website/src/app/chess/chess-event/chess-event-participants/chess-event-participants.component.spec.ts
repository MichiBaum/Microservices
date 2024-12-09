import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChessEventParticipantsComponent} from './chess-event-participants.component';

describe('ChessEventParticipantsComponent', () => {
  let component: ChessEventParticipantsComponent;
  let fixture: ComponentFixture<ChessEventParticipantsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessEventParticipantsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessEventParticipantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
