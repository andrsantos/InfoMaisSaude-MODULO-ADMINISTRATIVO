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
import { AuthService } from "../../../../services/auth/auth.service";
import { Subscription } from "rxjs";
import { fromEvent } from "rxjs";
import { GoogleMapsModule, GoogleMap, MapMarker } from "@angular/google-maps";
import { ClinicasService } from "../../../../services/clinicas/clinicas.service";
import { ToastrService } from "ngx-toastr";
import { ClinicaCreateResponse } from "../../../../models/clinicaModels/clinicaCreateResponse";
import { ClinicaReadResponse } from "src/app/models/clinicaModels/clinicaReadResponse";

declare var google: any;

@Component({
  selector: "app-cadastro-clinica",
  templateUrl: "./register-clinic.component.html",
  standalone: true,
  imports: [
    CommonModule,
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
    ButtonDirective,
  ],
})
export class RegisterClinicComponent {
  clinicaForm: FormGroup;
  errorMessage: string = "";
  successMessage: string = "";
  hasRegisteredClinic: boolean = false;

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
    private toastr: ToastrService
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

     const state = history.state as {
      hasRegisteredClinic?: boolean;
    } | null;
    console.log("State lido do history", state?.hasRegisteredClinic);
    console.log("Id da Clinica", localStorage.getItem('idDaClinica'));
    const idDaClinica = Number(localStorage.getItem('idDaClinica'));
    if(idDaClinica != null){
      this.pegarClinica(idDaClinica);
    }
    if(state?.hasRegisteredClinic){
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

  cadastrarClinica(): void {
    this.errorMessage = "";
    this.successMessage = "";

    if (this.clinicaForm.valid) {
      const formValue = this.clinicaForm.value;
      const especializacoesSelecionadas = Object.keys(
        formValue.especializacoes
      ).filter((key) => formValue.especializacoes[key]);

      const payload = {
        ...formValue,
        especializacoes: especializacoesSelecionadas,
      };

      console.log("Payload final a ser enviado:", payload);

      this.clinicasService.cadastrarClinica(payload).subscribe({
        next: (response: ClinicaCreateResponse) => {
          console.log("Clínica cadastrada com sucesso:", response);
          localStorage.setItem("possuiClinica", "true");
          localStorage.setItem("idDaClinica", JSON.stringify(response.idDaClinica));
          this.clinicaForm.reset();
          this.markerPosition = null;
          this.router.navigate(["/initial-page"], {
            state: {
              showSuccessToast: true,
              message: response.mensagemDeResposta,
            },
          });
        },
        error: (erro) => {
          console.error("Erro ao cadastrar clínica:", erro);
          const errorMsg =
            erro.error?.message ||
            erro.error ||
            "Falha ao cadastrar a clínica.";
          this.errorMessage = errorMsg;
          this.toastr.error(errorMsg, "Erro!");
        },
      });
    } else {
      this.clinicaForm.markAllAsTouched();
      this.errorMessage =
        "Formulário inválido. Verifique os campos obrigatórios (incluindo endereço e tipo).";
    }
  }

  pegarClinica(id: number){
    this.clinicasService.pegarClinica(id).subscribe({
      next: (response: ClinicaReadResponse) => {
      console.log("Nome da clínica", response.nome);
      },
      error: (erro) => {
      console.error("Erro ao pegar clínica:", erro);
      const errorMsg =
      erro.error?.message ||
      erro.error ||
      "Falha ao pegar a clínica.";
      this.errorMessage = errorMsg;
      this.toastr.error(errorMsg, "Erro!");
      },
    });
  }
}
