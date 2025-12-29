import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { profileRedirectGuard } from './profile-redirect.guard';

describe('profileRedirectGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => profileRedirectGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
