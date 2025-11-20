package com.example.demo.controller;

import com.example.demo.dto.request.TrilhaRequest;
import com.example.demo.dto.response.TrilhaResponse;
import com.example.demo.model.Trilha;
import com.example.demo.service.TrilhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trilhas")
@Tag(name = "api-trilhas")
public class TrilhaController {

    private final TrilhaService service;

    public TrilhaController(TrilhaService service) {
        this.service = service;
    }

    @Operation(summary = "Lista trilhas (paginado)")
    @GetMapping
    public ResponseEntity<Page<TrilhaResponse>> list(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        Sort s = Sort.by(sort.split(",")[0]).ascending();
        if (sort.toLowerCase().endsWith(",desc")) s = s.descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, s);
        Page<Trilha> page = service.readAll(pageable);
        return ResponseEntity.ok(page.map(TrilhaResponse::from));
    }

    @Operation(summary = "Cria uma trilha")
    @PostMapping
    public ResponseEntity<TrilhaResponse> create(@Valid @RequestBody TrilhaRequest req) {
        var t = new Trilha();
        t.setStatus(req.status());
        t.setConteudo(req.conteudo());
        t.setCompetencia(req.competencia());
        var salvo = service.create(t);
        return ResponseEntity.ok(TrilhaResponse.from(salvo));
    }

    @Operation(summary = "Busca trilha por ID")
    @GetMapping("/{id}")
    public ResponseEntity<TrilhaResponse> read(@PathVariable Long id) {
        var t = service.readById(id);
        return (t == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(TrilhaResponse.from(t));
    }

    @Operation(summary = "Atualiza trilha")
    @PutMapping("/{id}")
    public ResponseEntity<TrilhaResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TrilhaRequest req
    ) {
        var parcial = new Trilha();
        parcial.setStatus(req.status());
        parcial.setConteudo(req.conteudo());
        parcial.setCompetencia(req.competencia());

        var salvo = service.update(id, parcial);
        return (salvo == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(TrilhaResponse.from(salvo));
    }

    @Operation(summary = "Exclui trilha")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var existe = service.readById(id);
        if (existe == null) return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
