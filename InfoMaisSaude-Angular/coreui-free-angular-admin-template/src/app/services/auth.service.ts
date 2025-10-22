import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080'; 

  constructor(private http: HttpClient, private router: Router) { }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/usuarios/criar`, userData);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }
  logout(): void {
    localStorage.removeItem('authToken');
    this.router.navigate(['/login']);
  }
  private getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  public isAuthenticated(): boolean {
    const token = this.getToken();

    if (!token) {
      return false; 
    }

    try {
      const decodedToken: { exp: number } = jwtDecode(token);
      const expirationDate = new Date(0);
      expirationDate.setUTCSeconds(decodedToken.exp);

      if (expirationDate < new Date()) {
        console.log('Token expirado, deslogando...');
        this.logout(); 
        return false;
      }

      return true;

    } catch (error) {
      console.error('Token inválido ou malformado:', error);
      this.logout();
      return false;
    }
  }
}