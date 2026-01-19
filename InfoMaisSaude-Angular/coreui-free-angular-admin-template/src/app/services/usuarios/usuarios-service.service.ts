import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { Observable } from "rxjs";
import { UsuarioResponse } from "../../models/usuarioModels/usuarioResponse";
import { UsuarioDeleteResponse } from "../../models/usuarioModels/usuarioDeleteResponse";
import { UsuarioCreateResponse } from "../../models/usuarioModels/usuarioCreateResponse";

@Injectable({
  providedIn: "root",
})
export class UsuariosServiceService {
  private apiUrl = "/api";

  constructor(private http: HttpClient, private router: Router) {}

  listarUsuarios(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(
      `${this.apiUrl}/usuarios/listar`
    );
  }
  excluirUsuario(userId: number): Observable<UsuarioDeleteResponse> {
    const url = `${this.apiUrl}/usuarios/deletar/${userId}`;
    return this.http.delete<UsuarioDeleteResponse>(url);
  }
  cadastrarUsuario(dadosUsuario: any): Observable<UsuarioCreateResponse> {
    const url = `${this.apiUrl}/usuarios/criar`;
    return this.http.post<UsuarioCreateResponse>(url, dadosUsuario);
  }
}
