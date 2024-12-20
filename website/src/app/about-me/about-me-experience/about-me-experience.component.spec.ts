import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AboutMeExperienceComponent } from './about-me-experience.component';

describe('AboutMeExperienceComponent', () => {
  let component: AboutMeExperienceComponent;
  let fixture: ComponentFixture<AboutMeExperienceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AboutMeExperienceComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AboutMeExperienceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
