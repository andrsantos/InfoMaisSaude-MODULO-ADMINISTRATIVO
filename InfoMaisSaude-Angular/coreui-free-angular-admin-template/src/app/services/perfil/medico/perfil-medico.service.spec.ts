import { TestBed } from '@angular/core/testing';

import { PerfilMedicoService } from './perfil-medico.service';

describe('PerfilMedicoService', () => {
  let service: PerfilMedicoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PerfilMedicoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
