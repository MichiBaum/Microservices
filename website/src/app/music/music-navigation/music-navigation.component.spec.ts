import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MusicNavigationComponent } from './music-navigation.component';

describe('MusicNavigationComponent', () => {
  let component: MusicNavigationComponent;
  let fixture: ComponentFixture<MusicNavigationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MusicNavigationComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MusicNavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
