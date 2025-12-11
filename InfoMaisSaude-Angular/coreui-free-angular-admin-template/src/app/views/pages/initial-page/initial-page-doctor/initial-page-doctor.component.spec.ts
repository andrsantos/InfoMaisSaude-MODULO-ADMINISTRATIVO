import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InitialPageDoctorComponent } from './initial-page-doctor.component';

describe('InitialPageDoctorComponent', () => {
  let component: InitialPageDoctorComponent;
  let fixture: ComponentFixture<InitialPageDoctorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InitialPageDoctorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InitialPageDoctorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
