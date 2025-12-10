package com.Projeto.InfoMaisSaude.entities;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import com.Projeto.InfoMaisSaude.enums.Especializacao;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "tb_clinicas")
@Data
public class Clinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String endereco;

    @Column(nullable = true)
    private String site;

    @Column(nullable = false)
    private LocalTime horarioFuncionamentoInicio;

    @Column(nullable = false)
    private LocalTime horarioFuncionamentoFinal;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @ElementCollection(targetClass = Especializacao.class, fetch = FetchType.EAGER) 
    @CollectionTable(
            name = "tb_clinica_especializacoes", 
            joinColumns = @JoinColumn(name = "clinica_id") 
    )
    @Enumerated(EnumType.STRING) 
    @Column(name = "especializacao", nullable = false) 
    private Set<Especializacao> especializacoes = new HashSet<>(); 
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", unique = true, nullable = false)
    private Usuario usuario;
    
    
}
