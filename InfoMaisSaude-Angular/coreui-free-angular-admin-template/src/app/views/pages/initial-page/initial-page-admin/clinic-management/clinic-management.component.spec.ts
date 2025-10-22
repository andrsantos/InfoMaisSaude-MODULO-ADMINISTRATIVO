import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClinicManagementComponent } from './clinic-management.component';

describe('ClinicManagementComponent', () => {
  let component: ClinicManagementComponent;
  let fixture: ComponentFixture<ClinicManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ClinicManagementComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ClinicManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
