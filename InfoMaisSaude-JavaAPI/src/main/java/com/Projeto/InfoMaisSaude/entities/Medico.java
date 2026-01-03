package com.Projeto.InfoMaisSaude.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_medicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "especializacao_principal", nullable = false)
    private String especializacao;

    @Column(nullable = false, length = 20)
    private String telefone;

    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AgendaMedica> agenda = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "clinica_id", nullable = false) 
    private Clinica clinica;

    public void adicionarHorario(AgendaMedica horario) {
        agenda.add(horario);
        horario.setMedico(this);
    }
}