package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TRILHA_RESKI")
public class Trilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String status;

    @Column(nullable = false, length = 4000)
    private String conteudo;

    @Column(nullable = false, length = 255)
    private String competencia;


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getConteudo() { return conteudo; }

    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public String getCompetencia() { return competencia; }

    public void setCompetencia(String competencia) { this.competencia = competencia; }
}
