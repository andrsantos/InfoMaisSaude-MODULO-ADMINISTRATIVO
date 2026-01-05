package com.Projeto.InfoMaisSaude.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoCreateDTO;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoNomeDTO;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoResponseReadDTO;
import com.Projeto.InfoMaisSaude.dtos.medicoDTOs.MedicoUpdateDTO;
import com.Projeto.InfoMaisSaude.services.MedicosService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

    @Autowired
    MedicosService medicosService;

    

    @PostMapping("/criar")
    public ResponseEntity<?> criarNovoMedico(@RequestBody @Valid MedicoCreateDTO dto) {
        var medicoCriado = medicosService.criarMedico(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(medicoCriado);
    }

    @GetMapping("/listar")
    public ResponseEntity<?> listarMedicos() {
        var listaMedicos = medicosService.listarMedicos();
        return ResponseEntity.ok(listaMedicos);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<?> deletarMedico(@PathVariable Long id) {
        var resposta = medicosService.deletarMedico(id);
        return ResponseEntity.ok(resposta);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<?> atualizarMedico(@PathVariable Long id, @RequestBody @Valid MedicoUpdateDTO dto) {
        var medicoAtualizado = medicosService.atualizarMedico(id, dto);
        return ResponseEntity.ok(medicoAtualizado);
    }

    @GetMapping("/pegar/{id}")
    public ResponseEntity<?> pegarMedico(@PathVariable Long id) {
        var medico = medicosService.pegarMedico(id);
        return ResponseEntity.ok(medico);
    }

    @GetMapping("/pegarPorUsuario/{idUsuario}")
    public ResponseEntity<?> pegarMedicoPorUsuario(@PathVariable Long idUsuario) {
        var medico = medicosService.pegarMedicoPorUsuario(idUsuario);
        return ResponseEntity.ok(medico);
    }
    
    @GetMapping("/por-especialidade")
    public ResponseEntity<List<MedicoResponseReadDTO>> buscarPorEspecialidade(
            @RequestParam String especialidade
    ) {
        System.out.println("Especialidade: " + especialidade);
        var medicos = medicosService.buscarPorEspecialidade(especialidade);
        return ResponseEntity.ok(medicos);
    }

    @GetMapping("/por-nome/{clinicaId}")
    public ResponseEntity<List<MedicoNomeDTO>> buscarPorNome(@PathVariable Long clinicaId){
        var medicos = medicosService.buscaPorNome(clinicaId);
        return ResponseEntity.ok(medicos);
    }

}