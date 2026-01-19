import { Component, ElementRef, NgZone, ViewChild } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from "@angular/forms";
import { Router } from "@angular/router";
import {
  CardComponent,
  CardBodyComponent,
  CardHeaderComponent,
  ColComponent,
  FormDirective,
  FormControlDirective,
  FormLabelDirective,
  FormCheckComponent,
  FormCheckInputDirective,
  FormCheckLabelDirective,
  RowComponent,
  ButtonDirective,
} from "@coreui/angular";
import { AuthService } from "../../../../../services/auth/auth.service";
import { Subscription } from "rxjs";
import { fromEvent } from "rxjs";
import { GoogleMapsModule, GoogleMap, MapMarker } from "@angular/google-maps";
import { ClinicasService } from "../../../../../services/clinicas/clinicas.service";
import { ToastrService } from "ngx-toastr";
import { ClinicaReadResponse } from "../../../../../models/clinicaModels/clinicaReadResponse";
import { SolicitacoesService } from "../../../../../services/solicitacoes/solicitacoes.service";

declare var google: any;

@Component({
  selector: 'app-update-clinic',
  standalone: true,
  imports: [CommonModule,
    ReactiveFormsModule,
    CardComponent,
    GoogleMapsModule,
    CardHeaderComponent,
    CardBodyComponent,
    RowComponent,
    ColComponent,
    FormCheckComponent,
    FormCheckInputDirective,
    FormCheckLabelDirective,
    FormDirective,
    FormLabelDirective,
    FormControlDirective,
    ButtonDirective,],
  templateUrl: './update-clinic.component.html',
  styleUrl: './update-clinic.component.scss'
})
export class UpdateClinicComponent {

  clinicaForm: FormGroup;
  errorMessage: string = "";
  successMessage: string = "";
  hasRegisteredClinic: boolean = false;
  clinica: ClinicaReadResponse | undefined;
  userRole: string | null = null; 
  @ViewChild("searchBox") searchBox!: ElementRef;
  private autocomplete!: google.maps.places.Autocomplete;
  private geocoder!: google.maps.Geocoder;

  mapZoom = 15;
  mapCenter: google.maps.LatLngLiteral = { lat: -1.45502, lng: -48.5024 };
  markerPosition: google.maps.LatLngLiteral | null = null;
  mapOptions: google.maps.MapOptions = {
    disableDefaultUI: true,
    clickableIcons: false,
    mapTypeId: "roadmap",
  };

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private ngZone: NgZone,
    private clinicasService: ClinicasService,
    private toastr: ToastrService,
    private solicitacoesService: SolicitacoesService 
  ) {
    this.clinicaForm = this.fb.group({
      nome: ["", Validators.required],
      cnpj: ["", Validators.required],
      especializacoes: this.fb.group({
        MEDICA: [false],
        ODONTOLOGICA: [false],
      }),
      horarioFuncionamentoInicio: ["", Validators.required],
      horarioFuncionamentoFinal: ["", Validators.required],
      email: ["", [Validators.required, Validators.email]],
      site: [""],
      endereco: ["", Validators.required],
      telefone: ["", Validators.required],
      latitude: [null, Validators.required],
      longitude: [null, Validators.required],
    });
  }

  ngOnInit(): void {
    this.userRole = this.authService.getUserRole();

    const state = history.state as {
      hasRegisteredClinic?: boolean;
    } | null;
    console.log("State lido do history", state?.hasRegisteredClinic);
    console.log("Id da Clinica", localStorage.getItem('idDaClinica'));

    const idDaClinicaRaw = localStorage.getItem('idDaClinica');
    if (idDaClinicaRaw) {
      const idDaClinica = Number(idDaClinicaRaw);
      this.pegarClinica(idDaClinica);
    } else {
      console.log("ID da clínica não encontrado no localStorage.");
    }

    if (state?.hasRegisteredClinic) {
      this.hasRegisteredClinic = true;
    }

  }

  ngAfterViewInit(): void {
    if (typeof google !== "undefined" && google.maps && google.maps.places) {
      this.geocoder = new google.maps.Geocoder();
      this.autocomplete = new google.maps.places.Autocomplete(
        this.searchBox.nativeElement,
        {
          types: ["address"],
          componentRestrictions: { country: "br" },
        }
      );

      this.autocomplete.addListener(
        "place_changed",
        this.onPlaceChanged.bind(this)
      );
    } else {
      console.error(
        "Google Maps API não foi carregada corretamente. Verifique sua chave de API e a conexão com a internet."
      );
      this.errorMessage = "Erro ao carregar o mapa. Verifique a chave de API.";
    }
  }

  onPlaceChanged(): void {
    this.ngZone.run(() => {
      const place = this.autocomplete.getPlace();

      if (place.geometry && place.geometry.location) {
        const lat = place.geometry.location.lat();
        const lng = place.geometry.location.lng();

        this.clinicaForm.patchValue({
          endereco: this.searchBox.nativeElement.value,
          latitude: lat,
          longitude: lng,
        });

        this.mapCenter = { lat, lng };
        this.markerPosition = { lat, lng };
      } else {
        console.warn("Localização não selecionada da lista.");
      }
    });
  }

  pegarClinica(id: number) {
    console.log("Buscando clínica ID:", id);
    this.clinicasService.pegarClinica(id).subscribe({
      next: (response: ClinicaReadResponse) => {
        this.clinica = response;
        console.log("Dados recebidos:", this.clinica);
        this.preencherFormulario(this.clinica);
      },
      error: (erro) => {
        console.error("Erro ao pegar clínica:", erro);
        this.toastr.error("Erro ao carregar dados da clínica.");
      },
    });
  }

  preencherFormulario(dados: ClinicaReadResponse) {

    const inicio = dados.horarioFuncionamentoInicio ? dados.horarioFuncionamentoInicio.substring(0, 5) : '';
    const fim = dados.horarioFuncionamentoFinal ? dados.horarioFuncionamentoFinal.substring(0, 5) : '';

    this.clinicaForm.patchValue({
      nome: dados.nome,
      cnpj: dados.cnpj,
      email: dados.email,
      site: dados.site,
      endereco: dados.endereco,
      telefone: dados.telefone,
      horarioFuncionamentoInicio: inicio,
      horarioFuncionamentoFinal: fim,
      latitude: dados.latitude,
      longitude: dados.longitude
    });

    if (dados.especializacoes) {
      this.clinicaForm.get('especializacoes')?.patchValue({
        MEDICA: dados.especializacoes.includes('MEDICA'),
        ODONTOLOGICA: dados.especializacoes.includes('ODONTOLOGICA')
      });
    }

    if (dados.latitude && dados.longitude) {
      this.markerPosition = { lat: dados.latitude, lng: dados.longitude };
      this.mapCenter = { lat: dados.latitude, lng: dados.longitude };
    }
  }

  atualizarClinica() {
    if (this.clinicaForm.invalid) {
      this.toastr.warning("Preencha todos os campos obrigatórios.");
      return;
    }

    if (!this.clinica) return;

    const formValues = this.clinicaForm.value;

    const especializacoesArray = [];
    if (formValues.especializacoes.MEDICA) especializacoesArray.push('MEDICA');
    if (formValues.especializacoes.ODONTOLOGICA) especializacoesArray.push('ODONTOLOGICA');

    const payload = {
      ...formValues,
      especializacoes: especializacoesArray
    };

    if (this.userRole === 'ADMIN') {
      const payloadAdmin = { ...payload, id: this.clinica.id };

      this.clinicasService.atualizarClinica(this.clinica.id, payloadAdmin).subscribe({
        next: (res) => {
          this.toastr.success("Clínica atualizada com sucesso (Modo Admin)!");
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          console.error(err);
          this.toastr.error("Erro ao atualizar clínica.");
        }
      });
    }

    else if (this.userRole === 'CLINICA') {
      this.solicitacoesService.solicitarAlteracaoClinica(payload).subscribe({
        next: (res) => {
          this.toastr.info(
            "Suas alterações foram enviadas para aprovação da administração.",
            "Solicitação Enviada"
          );
          this.router.navigate(['/initial-page']);
        },
        error: (err) => {
          console.error(err);
          this.toastr.error("Erro ao enviar solicitação de alteração.");
        }
      });
    }
  }
}