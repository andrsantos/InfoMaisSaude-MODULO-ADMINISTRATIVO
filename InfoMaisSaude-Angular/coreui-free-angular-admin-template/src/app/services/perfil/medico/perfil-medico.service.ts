import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { MedicosService } from '../../medicos/medicos.service';
import { UsuariosServiceService } from '../../usuarios/usuarios-service.service';
import { PerfilMedico } from '../../../models/perfilModels/perfilMedicoModels/perfilMedico';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PerfilMedicoService {
  
  private apiUrl = "/api";

  constructor(private http: HttpClient, 
    private router: Router,
    private  medicosService: MedicosService,
    private usuariosService: UsuariosServiceService ) {}

  resgatarPerfilMedico(): Observable<PerfilMedico> {
        return this.http.get<PerfilMedico>(
          `${this.apiUrl}/api/perfil/medico`
        );
  }


  
}
