package com.Projeto.InfoMaisSaude.dtos.medicoDTOs;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicoCreateDTO {

    @NotBlank(message = "O nome do médico é obrigatório")
    String nome;

    @NotBlank(message = "A especialização é obrigatória")
    String especializacao;

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(regexp = "\\d+", message = "O telefone deve conter apenas números") 
    String telefone;

    @Valid 
    List<AgendaItemDTO> agenda;
    
    @NotBlank(message = "O login é obrigatório")
    String login;
    
    @NotBlank(message = "A senha é obrigatória")
    String senha;

    @NotNull(message = "O médico precisa estar vinculado a uma clínica")
    Long clinica_id;
    
}
