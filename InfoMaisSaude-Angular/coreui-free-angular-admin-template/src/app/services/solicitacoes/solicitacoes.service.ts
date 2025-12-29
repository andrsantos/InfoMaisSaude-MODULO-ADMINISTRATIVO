import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Solicitacao } from '../../models/solicitacaoModels/solicitacao.model';

@Injectable({
  providedIn: 'root'
})
export class SolicitacoesService {

  private apiUrl = 'http://localhost:8080/api/solicitacoes';

  constructor(private http: HttpClient) { }

  listarMeusPedidos(): Observable<Solicitacao[]> {
    return this.http.get<Solicitacao[]>(`${this.apiUrl}/meus-pedidos`);
  }

  listarPendentes(): Observable<Solicitacao[]> {
    return this.http.get<Solicitacao[]>(`${this.apiUrl}/pendentes`);
  }
  
  solicitarAlteracaoAgenda(payload: any): Observable<Solicitacao> {
      return this.http.post<Solicitacao>(`${this.apiUrl}/agenda`, payload);
  }

  solicitarAlteracaoDePerfil(payload: any): Observable<Solicitacao> {
    return this.http.post<Solicitacao>(`${this.apiUrl}/perfil`, payload);
  }

  aprovarSolicitacao(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/aprovar`, {});
  }

  rejeitarSolicitacao(id: number, motivo: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/rejeitar`, { motivo });
  }
}