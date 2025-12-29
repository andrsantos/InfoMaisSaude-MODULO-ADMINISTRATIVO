package com.Projeto.InfoMaisSaude.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.Projeto.InfoMaisSaude.enums.StatusSolicitacao;
import com.Projeto.InfoMaisSaude.enums.TipoSolicitacao;

@Entity
@Table(name = "tb_solicitacoes") 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_solicitacao", nullable = false)
    private TipoSolicitacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSolicitacao status;

    @Column(name = "dados_novos", columnDefinition = "TEXT", nullable = false)
    private String dadosNovos;

    @Column(name = "justificativa_medico", columnDefinition = "TEXT")
    private String justificativaMedico;

    @Column(name = "motivo_rejeicao", columnDefinition = "TEXT")
    private String motivoRejeicao;

    @ManyToOne
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "avaliador_id")
    private Usuario avaliador;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    @Column(name = "avaliado_em")
    private LocalDateTime avaliadoEm;

    @PrePersist
    public void prePersist() {
        if (this.criadoEm == null) {
            this.criadoEm = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusSolicitacao.PENDENTE;
        }
    }
}