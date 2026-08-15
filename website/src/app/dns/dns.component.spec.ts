import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DnsComponent } from './dns.component';

describe('DnsComponent', () => {
  let component: DnsComponent;
  let fixture: ComponentFixture<DnsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DnsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DnsComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
