import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessEventsListComponent } from './chess-events-list.component';

describe('ChessEventsListComponent', () => {
  let component: ChessEventsListComponent;
  let fixture: ComponentFixture<ChessEventsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessEventsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessEventsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
