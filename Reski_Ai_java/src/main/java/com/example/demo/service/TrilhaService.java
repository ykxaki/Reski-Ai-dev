package com.example.demo.service;

import com.example.demo.model.Trilha;
import com.example.demo.repository.TrilhaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TrilhaService {

    private final TrilhaRepository repo;

    public TrilhaService(TrilhaRepository repo) {
        this.repo = repo;
    }

    public Trilha create(Trilha t) {
        return repo.save(t);
    }

    public Page<Trilha> readAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Trilha readById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public Trilha update(Long id, Trilha parcial) {
        return repo.findById(id).map(t -> {
            if (parcial.getStatus() != null)      t.setStatus(parcial.getStatus());
            if (parcial.getConteudo() != null)    t.setConteudo(parcial.getConteudo());
            if (parcial.getCompetencia() != null) t.setCompetencia(parcial.getCompetencia());
            return repo.save(t);
        }).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
