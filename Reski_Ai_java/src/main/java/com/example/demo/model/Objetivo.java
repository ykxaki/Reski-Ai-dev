package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "OBJETIVO_RESKI")
public class Objetivo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String cargo;

    @Column(nullable = false, length = 150)
    private String area;

    @Column(nullable = false, length = 4000)
    private String descricao;

    @Column(nullable = false, length = 4000)
    private String demanda;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getCargo() { return cargo; }

    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getArea() { return area; }

    public void setArea(String area) { this.area = area; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getDemanda() { return demanda; }

    public void setDemanda(String demanda) { this.demanda = demanda; }
}
