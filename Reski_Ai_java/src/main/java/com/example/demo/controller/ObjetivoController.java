package com.example.demo.controller;

import com.example.demo.dto.request.ObjetivoRequest;
import com.example.demo.dto.response.ObjetivoResponse;
import com.example.demo.model.Objetivo;
import com.example.demo.service.ObjetivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/objetivos")
@Tag(name = "api-objetivos")
public class ObjetivoController {

    private final ObjetivoService service;

    public ObjetivoController(ObjetivoService service) {
        this.service = service;
    }

    @Operation(summary = "Lista objetivos (paginado)")
    @GetMapping
    public ResponseEntity<Page<ObjetivoResponse>> list(
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id,asc") String sort
    ) {
        Sort s = Sort.by(sort.split(",")[0]).ascending();
        if (sort.toLowerCase().endsWith(",desc")) s = s.descending();

        Pageable pageable = PageRequest.of(pageNumber, pageSize, s);
        Page<Objetivo> page = service.readAll(pageable);
        return ResponseEntity.ok(page.map(ObjetivoResponse::from));
    }

    @Operation(summary = "Cria objetivo")
    @PostMapping
    public ResponseEntity<ObjetivoResponse> create(@Valid @RequestBody ObjetivoRequest req) {
        var o = new Objetivo();
        o.setCargo(req.cargo());
        o.setArea(req.area());
        o.setDescricao(req.descricao());
        o.setDemanda(req.demanda());
        var salvo = service.create(o);
        return ResponseEntity.ok(ObjetivoResponse.from(salvo));
    }

    @Operation(summary = "Busca objetivo por ID")
    @GetMapping("/{id}")
    public ResponseEntity<ObjetivoResponse> read(@PathVariable Long id) {
        var o = service.readById(id);
        return (o == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(ObjetivoResponse.from(o));
    }

    @Operation(summary = "Atualiza objetivo")
    @PutMapping("/{id}")
    public ResponseEntity<ObjetivoResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ObjetivoRequest req
    ) {
        var parcial = new Objetivo();
        parcial.setCargo(req.cargo());
        parcial.setArea(req.area());
        parcial.setDescricao(req.descricao());
        parcial.setDemanda(req.demanda());

        var salvo = service.update(id, parcial);
        return (salvo == null) ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(ObjetivoResponse.from(salvo));
    }

    @Operation(summary = "Exclui objetivo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var existe = service.readById(id);
        if (existe == null) return ResponseEntity.notFound().build();
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
