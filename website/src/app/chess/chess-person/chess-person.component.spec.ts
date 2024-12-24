import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessPersonComponent } from './chess-person.component';

describe('ChessPersonComponent', () => {
  let component: ChessPersonComponent;
  let fixture: ComponentFixture<ChessPersonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessPersonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessPersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
