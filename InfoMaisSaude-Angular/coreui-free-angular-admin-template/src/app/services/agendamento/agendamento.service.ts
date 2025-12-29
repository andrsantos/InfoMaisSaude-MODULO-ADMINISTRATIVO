import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Consulta } from '../../models/consultaModels/consulta.model';
@Injectable({
  providedIn: 'root'
})
export class AgendamentoService {

  private apiUrl = 'http://localhost:8080/api/agendamentos';

  constructor(private http: HttpClient) { }

  listarConsultas(data?: string, medicoId?: number): Observable<Consulta[]> {
    let params = new HttpParams();
    
    if (data) {
      params = params.set('data', data);
    }
    
    if (medicoId) {
      params = params.set('medicoIdFiltro', medicoId.toString());
    }

    return this.http.get<Consulta[]>(this.apiUrl, { params });
  }
}