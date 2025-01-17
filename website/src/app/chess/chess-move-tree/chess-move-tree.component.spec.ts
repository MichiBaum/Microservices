import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessMoveTreeComponent } from './chess-move-tree.component';

describe('ChessMoveTreeComponent', () => {
  let component: ChessMoveTreeComponent;
  let fixture: ComponentFixture<ChessMoveTreeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessMoveTreeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessMoveTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
