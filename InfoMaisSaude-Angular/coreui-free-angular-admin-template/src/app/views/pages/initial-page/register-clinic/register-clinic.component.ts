import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CardComponent, CardBodyComponent, CardHeaderComponent, ColComponent, FormDirective, FormControlDirective, FormLabelDirective, 
  FormCheckComponent,
  FormCheckInputDirective,
  FormCheckLabelDirective,
   RowComponent, ButtonDirective } from '@coreui/angular';
import { AuthService } from '../../../../services/auth.service';

@Component({
  selector: 'app-cadastro-clinica',
  templateUrl: './register-clinic.component.html',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, CardComponent, 
    CardHeaderComponent, CardBodyComponent, RowComponent, ColComponent, 
    FormCheckComponent,
    FormCheckInputDirective,
    FormCheckLabelDirective,
    FormDirective, FormLabelDirective, FormControlDirective, ButtonDirective]
})
export class RegisterClinicComponent {

  clinicaForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
this.clinicaForm = this.fb.group({
      nome: ['', Validators.required],
      cnpj: ['', Validators.required],
      especializacoes: this.fb.group({
        MEDICA: [false],
        ODONTOLOGICA: [false]
      }),
      horarioFuncionamentoInicio: ['', Validators.required],
      horarioFuncionamentoFinal: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      site: [''], 
      endereco: ['', Validators.required],
      telefone: ['', Validators.required],
    });
  }

  // onSubmit(): void {
  //   this.errorMessage = '';
  //   this.successMessage = '';

  //   if (this.clinicaForm.valid) {
  //     this.authService.cadastrarClinica(this.clinicaForm.value).subscribe({
  //       next: (response) => {
  //         this.successMessage = `Clínica "${response.nome}" cadastrada com sucesso!`;
  //         this.clinicaForm.reset(); // Limpa o formulário
  //       },
  //       error: (err) => {
  //         this.errorMessage = 'Erro ao cadastrar a clínica. Verifique os dados e tente novamente.';
  //         console.error(err);
  //       }
  //     });
  //   }
  // }
}
