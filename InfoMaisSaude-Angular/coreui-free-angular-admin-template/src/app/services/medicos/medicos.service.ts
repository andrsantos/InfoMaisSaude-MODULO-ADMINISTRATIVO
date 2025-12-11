import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { MedicoReadResponse } from '../../models/medicoModels/medicoReadResponse';
import { MedicoDeleteResponse } from '../../models/medicoModels/medicoDeleteResponse';
import { MedicoCreateResponse } from '../../models/medicoModels/medicoCreateResponse';
import { MedicoUpdateResponse } from '../../models/medicoModels/medicoUpdateResponse';

@Injectable({
  providedIn: 'root'
})
export class MedicosService {
  private apiUrl = 'http://localhost:8080';
  
  constructor(private http: HttpClient, private router: Router) {}  

  listarMedicos(): Observable<MedicoReadResponse[]> {
      const url = `${this.apiUrl}/api/medicos/listar`;
      return this.http.get<MedicoReadResponse[]>(url);
    }
  
  excluirMedico(medicoId: number): Observable<MedicoDeleteResponse> {
      const url = `${this.apiUrl}/api/medicos/deletar/${medicoId}`;
      return this.http.delete<MedicoDeleteResponse>(url);
    }
  
  cadastrarMedico(dadosMedico: any): Observable<MedicoCreateResponse> {
      const url = `${this.apiUrl}/api/medicos/criar`;
      return this.http.post<MedicoCreateResponse>(url, dadosMedico);
    }
  
  atualizarMedico(
      medicoId: number,
      dadosMedico: any
    ): Observable<MedicoUpdateResponse> {
      const url = `${this.apiUrl}/api/clinicas/atualizar/${medicoId}`;
      return this.http.put<MedicoUpdateResponse>(url, dadosMedico);
    }
  
  pegarMedico(medicoId: number): Observable<MedicoReadResponse> {
      const url = `${this.apiUrl}/api/medicos/pegar/${medicoId}`;
      return this.http.get<MedicoReadResponse>(url);
    }
}
