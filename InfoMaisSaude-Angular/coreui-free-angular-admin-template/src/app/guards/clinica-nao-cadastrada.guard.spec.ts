import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { clinicaNaoCadastradaGuard } from './clinica-nao-cadastrada.guard';

describe('clinicaNaoCadastradaGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => clinicaNaoCadastradaGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
