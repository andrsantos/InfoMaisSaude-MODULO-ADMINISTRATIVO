import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { clinicaCadastradaGuard } from './clinica-cadastrada.guard';

describe('clinicaCadastradaGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => clinicaCadastradaGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
