import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessNewsComponent } from './chess-news.component';

describe('ChessNewsComponent', () => {
  let component: ChessNewsComponent;
  let fixture: ComponentFixture<ChessNewsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessNewsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessNewsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
