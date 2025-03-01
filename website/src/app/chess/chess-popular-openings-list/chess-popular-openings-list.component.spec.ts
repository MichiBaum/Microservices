import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessPopularOpeningsListComponent } from './chess-popular-openings-list.component';

describe('ChessPopularOpeningsListComponent', () => {
  let component: ChessPopularOpeningsListComponent;
  let fixture: ComponentFixture<ChessPopularOpeningsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessPopularOpeningsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessPopularOpeningsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
