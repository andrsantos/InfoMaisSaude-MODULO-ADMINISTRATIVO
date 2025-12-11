package com.Projeto.InfoMaisSaude.dtos.medicoDTOs;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicoUpdateDTO {

    
    @NotBlank(message = "O nome do médico é obrigatório")
    String nome;

    @NotBlank(message = "A especialização é obrigatória")
    String especializacao;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d+", message = "O telefone deve conter apenas números") 
    String telefone;

    @NotEmpty(message = "A agenda não pode estar vazia") 
    @Valid 
    List<AgendaItemDTO> agenda;
    
}
