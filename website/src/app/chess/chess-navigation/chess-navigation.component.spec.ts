import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ChessNavigationComponent} from './chess-navigation.component';

describe('ChessNavigationComponent', () => {
  let component: ChessNavigationComponent;
  let fixture: ComponentFixture<ChessNavigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChessNavigationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChessNavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
