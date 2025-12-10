import { TestBed } from '@angular/core/testing';

import { ClinicasService } from './clinicas.service';

describe('ClinicasService', () => {
  let service: ClinicasService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ClinicasService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
