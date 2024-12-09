import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChessUpdateEventComponent} from './chess-update-event.component';

describe('ChessUpdateEventComponent', () => {
  let component: ChessUpdateEventComponent;
  let fixture: ComponentFixture<ChessUpdateEventComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessUpdateEventComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessUpdateEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
