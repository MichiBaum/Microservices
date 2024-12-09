import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChessUpdatePersonComponent} from './chess-update-person.component';

describe('ChessUpdatePersonComponent', () => {
  let component: ChessUpdatePersonComponent;
  let fixture: ComponentFixture<ChessUpdatePersonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessUpdatePersonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessUpdatePersonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
