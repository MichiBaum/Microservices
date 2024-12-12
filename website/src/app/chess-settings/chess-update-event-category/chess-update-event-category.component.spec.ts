import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessUpdateEventCategoryComponent } from './chess-update-event-category.component';

describe('ChessUpdateEventCategoryComponent', () => {
  let component: ChessUpdateEventCategoryComponent;
  let fixture: ComponentFixture<ChessUpdateEventCategoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessUpdateEventCategoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessUpdateEventCategoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
