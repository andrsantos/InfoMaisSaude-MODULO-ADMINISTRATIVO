import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Router, RouterLink } from "@angular/router"; // Para que os botões possam ter links

import {
  RowComponent,
  ColComponent,
  CardComponent,
  CardBodyComponent,
  CardImgDirective,
  CardTitleDirective,
  CardTextDirective,
  ButtonDirective,
} from "@coreui/angular";
import { ToastrService } from "ngx-toastr";

@Component({
  selector: "app-initial-page",
  templateUrl: "./initial-page.component.html",
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    RowComponent,
    ColComponent,
    CardComponent,
    CardBodyComponent,
    CardImgDirective,
    CardTitleDirective,
    CardTextDirective,
    ButtonDirective,
  ],
})
export class InitialPageComponent {
  constructor(private Router: Router, private toastr: ToastrService) {}
  ngOnInit(): void {
    const state = history.state as {
      showSuccessToast?: boolean;
      message?: string;
    } | null;

    console.log("State lido do history:", state);

    if (state?.showSuccessToast && state?.message) {
      this.toastr.success(state.message, "Sucesso!");
      history.replaceState(
        { ...history.state, showSuccessToast: undefined, message: undefined },
        ""
      );
    } else {
      console.log("Nenhum state de toast encontrado no history.");
    }
  }

  goToRegisterClinic(): void {
    const possuiClinica = localStorage.getItem("possuiClinica");
    if(possuiClinica == 'true'){
    this.Router.navigate(["/register-clinic"], {
      state:{
        hasRegisteredClinic: true
      }
    });
    } 
  }
  goToDoctorsList(): void {
    this.Router.navigate(["/doctors-list"]);
  }
}
