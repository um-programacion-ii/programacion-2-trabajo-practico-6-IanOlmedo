package com.TP_6.Sistema_Microservicios.data_service.service;

import com.TP_6.Sistema_Microservicios.data_service.entity.Inventario;
import com.TP_6.Sistema_Microservicios.data_service.repository.InventarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<Inventario> obtenerProductosConStockBajo(Integer umbral) {
        return inventarioRepository.findByCantidadLessThanEqual(umbral);
    }

    public Inventario actualizarStock(Long id, Integer nuevaCantidad) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inventario no encontrado con id: " + id));
        inventario.setCantidad(nuevaCantidad);
        inventario.setFechaActualizacion(LocalDateTime.now());
        return inventario;
    }
}
