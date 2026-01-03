package com.Projeto.InfoMaisSaude.dtos.clinicaDTOs;

import java.time.LocalTime;
import java.util.Set;
import com.Projeto.InfoMaisSaude.enums.Especializacao;
import lombok.Data;
import lombok.NoArgsConstructor; 
import lombok.AllArgsConstructor; 

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class ClinicaCreateDTO {

    private String nome;
    private String cnpj;
    private String email;
    private String telefone;
    private String endereco;
    private String site; 
    private Set<Especializacao> especializacoes; 
    private LocalTime horarioFuncionamentoInicio;
    private LocalTime horarioFuncionamentoFinal;
    private Double latitude;
    private Double longitude;

    
}