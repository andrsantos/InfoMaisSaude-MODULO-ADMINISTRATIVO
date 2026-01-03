import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { ClinicaCreateResponse } from "../../models/clinicaModels/clinicaCreateResponse";
import { catchError, map, Observable, of } from "rxjs";
import { ClinicaReadResponse } from "../../models/clinicaModels/clinicaReadResponse";
import { ClinicaDeleteResponse } from "../../models/clinicaModels/clinicaDeleteResponse";
import { ClinicaUpdateResponse } from "../../models/clinicaModels/clinicaUpdateResponse";
import { ConsultasPorClinicaReadResponse } from "../../models/consultaModels/consultasPorClinica.model";

@Injectable({
  providedIn: "root",
})
export class ClinicasService {
  private apiUrl = "http://localhost:8080";

  constructor(private http: HttpClient, private router: Router) {}

  listarClinicas(): Observable<ClinicaReadResponse[]> {
    const url = `${this.apiUrl}/api/clinicas/listar`;
    return this.http.get<ClinicaReadResponse[]>(url);
  }

  excluirClinica(clinicaId: number): Observable<ClinicaDeleteResponse> {
    const url = `${this.apiUrl}/api/clinicas/deletar/${clinicaId}`;
    return this.http.delete<ClinicaDeleteResponse>(url);
  }

  cadastrarClinica(dadosClinica: any): Observable<ClinicaCreateResponse> {
    const url = `${this.apiUrl}/api/clinicas/criar`;
    return this.http.post<ClinicaCreateResponse>(url, dadosClinica);
  }

  atualizarClinica(
    clinicaId: number,
    dadosClinica: any
  ): Observable<ClinicaUpdateResponse> {
    const url = `${this.apiUrl}/api/clinicas/atualizar/${clinicaId}`;
    return this.http.put<ClinicaUpdateResponse>(url, dadosClinica);
  }

  pegarClinica(clinicaId: number): Observable<ClinicaReadResponse> {
    const url = `${this.apiUrl}/api/clinicas/pegar/${clinicaId}`;
    return this.http.get<ClinicaReadResponse>(url);
  }

  verificarCadastro(): Observable<boolean> {
  const idSalvo = localStorage.getItem('idDaClinica'); 
    if (!idSalvo) {
      return of(false);
    }
    return this.pegarClinica(Number(idSalvo)).pipe(
      map((dados: any) => {
        return !!dados; 
      }),
      catchError(() => {
        return of(false); 
      })
    );
  }

listarConsultasPorClinica(idDaClinica: any, data?: string) {
    let params = new HttpParams();
    
    if (data) {
      params = params.set('data', data);
    }

    return this.http.get<ConsultasPorClinicaReadResponse[]>(
      `${this.apiUrl}/api/agendamentos/consultas-clinica/${idDaClinica}`, 
      { params }
    );
  }
}
