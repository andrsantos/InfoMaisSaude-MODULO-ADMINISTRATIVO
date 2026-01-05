package com.Projeto.InfoMaisSaude.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseDeleteDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaResponseUpdateDTO;
import com.Projeto.InfoMaisSaude.dtos.clinicaDTOs.ClinicaUpdateDTO;
import com.Projeto.InfoMaisSaude.exceptions.ClinicaJaExisteException;
import com.Projeto.InfoMaisSaude.exceptions.ClinicaNaoExisteException;
import com.Projeto.InfoMaisSaude.exceptions.PermissaoException;
import com.Projeto.InfoMaisSaude.services.ClinicaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clinicas")
public class ClinicaController {

    @Autowired
    ClinicaService clinicaService;

    @PostMapping("/criar")
    public ResponseEntity<?> criarNovaClinica(@RequestBody @Valid ClinicaCreateDTO dto){
    try{
    ClinicaResponseCreateDTO novaClinica = clinicaService.criarClinica(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(novaClinica);
    }
    catch(ClinicaJaExisteException e){
    return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    catch(PermissaoException e){
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    catch(RuntimeException e){
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado.");       
    }
    }

    @GetMapping ("/listar")
    public ResponseEntity<?> listarClinicas(){
        return ResponseEntity.ok().body(clinicaService.listarClinicas());
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletarClinica(@PathVariable Long id){
    try{
        ClinicaResponseDeleteDTO clinicaDeletada = clinicaService.deletarClinica(id);
        return ResponseEntity.ok().body(clinicaDeletada);
    } 
    catch(UsernameNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    }

    @PutMapping ("/atualizar/{id}")
    public ResponseEntity<?> atualizarClinica(@PathVariable Long id, @RequestBody @Valid ClinicaUpdateDTO dto){
    try{
        ClinicaResponseUpdateDTO clinicaAtualizada = clinicaService.atualizarClinica(id, dto);
        return ResponseEntity.ok().body(clinicaAtualizada);
    } 
    catch(UsernameNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    catch(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado.");
    }
    }

    @GetMapping("/pegar/{id}")
    public ResponseEntity<?> pegarClinica(@PathVariable Long id){
    try{
    ClinicaResponseReadDTO clinicaRecuperada = clinicaService.pegarClinica(id);
    return ResponseEntity.ok().body(clinicaRecuperada);
    }
    catch(ClinicaNaoExisteException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    }

    @GetMapping("/listar-resumo")
    public ResponseEntity<?> listarClinicasResumo(){
        return ResponseEntity.ok().body(clinicaService.listarClinicasResumo());
    }

    @GetMapping("/listar-especialidades/{clinicaId}")
    public ResponseEntity<?> listarEspecialidadesClinica(@PathVariable Long clinicaId){

        Set<String> especialidades = clinicaService.listarEspecialidadesClinica(clinicaId);
        List<String> especialidadesLista = new ArrayList<>();

        especialidades.forEach(especialidade -> {
            especialidadesLista.add(especialidade);
        });

        return ResponseEntity.ok().body(especialidadesLista);

        
    }
    
}
