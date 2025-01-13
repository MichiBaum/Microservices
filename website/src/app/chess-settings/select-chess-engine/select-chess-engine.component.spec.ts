import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelectChessEngineComponent} from './select-chess-engine.component';

describe('SelectChessEngineComponent', () => {
  let component: SelectChessEngineComponent;
  let fixture: ComponentFixture<SelectChessEngineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelectChessEngineComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelectChessEngineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
