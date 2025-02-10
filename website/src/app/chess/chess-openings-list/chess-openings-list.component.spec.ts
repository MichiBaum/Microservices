import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessOpeningsListComponent } from './chess-openings-list.component';

describe('ChessOpeningsListComponent', () => {
  let component: ChessOpeningsListComponent;
  let fixture: ComponentFixture<ChessOpeningsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessOpeningsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessOpeningsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
