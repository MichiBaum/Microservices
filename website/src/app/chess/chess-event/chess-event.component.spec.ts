import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessEventComponent } from './chess-event.component';

describe('ChessEventsComponent', () => {
  let component: ChessEventComponent;
  let fixture: ComponentFixture<ChessEventComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessEventComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
