import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChessUpdateAccountComponent } from './chess-update-account.component';

describe('ChessUpdateAccountComponent', () => {
  let component: ChessUpdateAccountComponent;
  let fixture: ComponentFixture<ChessUpdateAccountComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessUpdateAccountComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessUpdateAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
