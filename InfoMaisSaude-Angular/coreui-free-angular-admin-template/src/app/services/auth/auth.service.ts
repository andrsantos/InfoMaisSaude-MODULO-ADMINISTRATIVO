import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { Router } from "@angular/router";
import { jwtDecode } from "jwt-decode";

interface DecodedToken {
  sub: string;
  exp: number;
  iat: number;
  role: string;
  id: number;
}

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private apiUrl = "http://localhost:8080";

  constructor(private http: HttpClient, private router: Router) {}

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/usuarios/criar`, userData);
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials);
  }

  logout(): void {
    localStorage.removeItem("authToken");
    this.router.navigate(["/login"]);
  }

  private getToken(): string | null {
    return localStorage.getItem("authToken");
  }

  public getDecodedToken(): DecodedToken | null {
    const token = this.getToken();

    if (!token) {
      return null;
    }

    try {
      return jwtDecode<DecodedToken>(token);
    } catch (error) {
      console.error("Erro ao decodificar token:", error);
      return null;
    }
  }

  public getUserRole(): string | null {
    const decodedToken = this.getDecodedToken();
    return decodedToken ? decodedToken.role : null;
  }

  public getCurrentUser() {
    const decodedToken = this.getDecodedToken();
    
    if (!decodedToken) return null;

    return {
      id: decodedToken.id,
      login: decodedToken.sub,
      role: decodedToken.role
    };
  }

  public isAuthenticated(): boolean {
    const token = this.getToken();

    if (!token) return false;

    try {
      const decodedToken = jwtDecode<{ exp: number }>(token);
      const expirationDate = new Date(0);
      expirationDate.setUTCSeconds(decodedToken.exp);

      if (expirationDate < new Date()) {
        this.logout();
        return false;
      }

      return true;
    } catch (error) {
      this.logout();
      return false;
    }
  }

  public getLoggedInUserLogin() {
    const decodedToken = this.getDecodedToken();
    return decodedToken ? decodedToken.sub : null;
  }

  public hasRegisteredClinic(): boolean {
    console.log("Local Storage", localStorage.getItem("possuiClinica"));
    const flag = localStorage.getItem("possuiClinica");
    return flag == 'true';
  }
}
