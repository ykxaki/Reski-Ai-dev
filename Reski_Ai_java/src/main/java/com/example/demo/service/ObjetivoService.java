package com.example.demo.service;

import com.example.demo.model.Objetivo;
import com.example.demo.repository.ObjetivoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ObjetivoService {

    private final ObjetivoRepository repo;

    public ObjetivoService(ObjetivoRepository repo) {
        this.repo = repo;
    }

    public Objetivo create(Objetivo o) {
        return repo.save(o);
    }

    public Page<Objetivo> readAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Objetivo readById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Objetivo update(Long id, Objetivo parcial) {
        return repo.findById(id).map(o -> {
            if (parcial.getCargo() != null)      o.setCargo(parcial.getCargo());
            if (parcial.getArea() != null)       o.setArea(parcial.getArea());
            if (parcial.getDescricao() != null)  o.setDescricao(parcial.getDescricao());
            if (parcial.getDemanda() != null)    o.setDemanda(parcial.getDemanda());
            return repo.save(o);
        }).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
