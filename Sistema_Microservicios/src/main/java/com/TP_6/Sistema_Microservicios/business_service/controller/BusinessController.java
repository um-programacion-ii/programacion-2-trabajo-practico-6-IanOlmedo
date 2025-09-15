package com.TP_6.Sistema_Microservicios.business_service.controller;

import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoRequest;
import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoResponse;
import com.TP_6.Sistema_Microservicios.business_service.service.ProductoBusinessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class BusinessController {

    private final ProductoBusinessService service;

    public BusinessController(ProductoBusinessService service) {
        this.service = service;
    }

    // Health check simple (opcional)
    @GetMapping("/health")
    public String health() {
        return "OK-BUSINESS";
    }

    @GetMapping
    public List<ProductoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ProductoResponse porId(@PathVariable Long id) {
        return service.porId(id);
    }

    @GetMapping("/categoria/{nombre}")
    public List<ProductoResponse> porCategoria(@PathVariable String nombre) {
        return service.porCategoria(nombre);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crear(@Valid @RequestBody ProductoRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
