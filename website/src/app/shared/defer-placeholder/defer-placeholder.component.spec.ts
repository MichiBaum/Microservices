import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DeferPlaceholderComponent} from './defer-placeholder.component';

describe('DeferPlaceholderComponent', () => {
  let component: DeferPlaceholderComponent;
  let fixture: ComponentFixture<DeferPlaceholderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeferPlaceholderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeferPlaceholderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
