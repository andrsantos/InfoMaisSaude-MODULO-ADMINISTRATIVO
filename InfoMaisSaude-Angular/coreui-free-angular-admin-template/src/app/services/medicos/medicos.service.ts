import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { MedicoReadResponse } from '../../models/medicoModels/medicoReadResponse';
import { MedicoDeleteResponse } from '../../models/medicoModels/medicoDeleteResponse';
import { MedicoCreateResponse } from '../../models/medicoModels/medicoCreateResponse';
import { MedicoUpdateResponse } from '../../models/medicoModels/medicoUpdateResponse';
import { MedicoNomeReadResponse } from '../../models/medicoModels/medicoNomeReadResponse';

@Injectable({
  providedIn: 'root'
})
export class MedicosService {
  private apiUrl = '/api';
  
  constructor(private http: HttpClient, private router: Router) {}  

  listarMedicos(): Observable<MedicoReadResponse[]> {
      const url = `${this.apiUrl}/medicos/listar`;
      return this.http.get<MedicoReadResponse[]>(url);
    }
  listarMedicosPorClinica(clinicaId: any): Observable<MedicoReadResponse[]> {
      const url = `${this.apiUrl}/medicos/listar/por-clinica/${clinicaId}`;
      return this.http.get<MedicoReadResponse[]>(url);
    }
  
  excluirMedico(medicoId: number): Observable<MedicoDeleteResponse> {
      const url = `${this.apiUrl}/medicos/deletar/${medicoId}`;
      return this.http.delete<MedicoDeleteResponse>(url);
    }
  
  cadastrarMedico(dadosMedico: any): Observable<MedicoCreateResponse> {
      const url = `${this.apiUrl}/medicos/criar`;
      return this.http.post<MedicoCreateResponse>(url, dadosMedico);
    }
  
  atualizarMedico(
      medicoId: number,
      dadosMedico: any
    ): Observable<MedicoUpdateResponse> {
      const url = `${this.apiUrl}/medicos/atualizar/${medicoId}`;
      return this.http.put<MedicoUpdateResponse>(url, dadosMedico);
    }
  
  pegarMedico(medicoId: number): Observable<MedicoReadResponse> {
      const url = `${this.apiUrl}/medicos/pegar/${medicoId}`;
      return this.http.get<MedicoReadResponse>(url);
    }

  pegarMedicoPorUsuarioId(usuarioId: number): Observable<MedicoReadResponse> {
    const url = `${this.apiUrl}/medicos/pegarPorUsuario/${usuarioId}`;
    return this.http.get<MedicoReadResponse>(url);
  }

  pegarIdDoMedicoLogado(): number {
    const usuarioId = localStorage.getItem("usuarioId");
    return usuarioId ? parseInt(usuarioId, 10) : 0;
  }

  pegarMedicosNome(clinicaId: any): Observable<MedicoNomeReadResponse>{
    const url = `${this.apiUrl}/medicos/por-nome/${clinicaId}`;
    return this.http.get<MedicoNomeReadResponse>(url);
  }

}
