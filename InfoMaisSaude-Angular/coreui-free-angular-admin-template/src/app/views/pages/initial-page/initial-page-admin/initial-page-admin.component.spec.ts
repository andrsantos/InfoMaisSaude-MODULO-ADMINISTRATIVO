import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InitialPageAdminComponent } from './initial-page-admin.component';

describe('InitialPageAdminComponent', () => {
  let component: InitialPageAdminComponent;
  let fixture: ComponentFixture<InitialPageAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InitialPageAdminComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InitialPageAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
