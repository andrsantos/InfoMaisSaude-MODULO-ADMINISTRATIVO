import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  
  const authToken = localStorage.getItem('token');

  if (authToken) {
    const clonedReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${authToken}`)
    });

    return next(clonedReq);
  }

  return next(req);
};