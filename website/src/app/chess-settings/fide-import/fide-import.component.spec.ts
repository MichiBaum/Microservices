import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FideImportComponent} from './fide-import.component';

describe('FideImportComponent', () => {
  let component: FideImportComponent;
  let fixture: ComponentFixture<FideImportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FideImportComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FideImportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
