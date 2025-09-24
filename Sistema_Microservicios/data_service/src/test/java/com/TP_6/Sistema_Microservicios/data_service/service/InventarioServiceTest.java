package com.TP_6.Sistema_Microservicios.data_service.service;

import com.TP_6.Sistema_Microservicios.data_service.entity.Inventario;
import com.TP_6.Sistema_Microservicios.data_service.repository.InventarioRepository;
import com.TP_6.Sistema_Microservicios.data_service.service.InventarioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventarioServiceTest {

    @Test
    void obtenerProductosConStockBajo_ok() {
        InventarioRepository repo = mock(InventarioRepository.class);
        when(repo.findByCantidadLessThanEqual(5)).thenReturn(List.of(new Inventario(), new Inventario()));
        InventarioService service = new InventarioService(repo);

        assertEquals(2, service.obtenerProductosConStockBajo(5).size());
        verify(repo).findByCantidadLessThanEqual(5);
    }

    @Test
    void actualizarStock_ok_seteaFecha() {
        Inventario inv = new Inventario(); inv.setId(10L); inv.setCantidad(3);
        InventarioRepository repo = mock(InventarioRepository.class);
        when(repo.findById(10L)).thenReturn(Optional.of(inv));

        InventarioService service = new InventarioService(repo);
        Inventario out = service.actualizarStock(10L, 20);

        assertEquals(20, out.getCantidad());
        assertNotNull(out.getFechaActualizacion());
        assertTrue(out.getFechaActualizacion().isBefore(LocalDateTime.now().plusSeconds(2)));
        verify(repo, never()).save(any()); // la impl no llama save()
    }

    @Test
    void actualizarStock_noExiste_lanza() {
        InventarioRepository repo = mock(InventarioRepository.class);
        when(repo.findById(404L)).thenReturn(Optional.empty());
        InventarioService service = new InventarioService(repo);

        assertThrows(EntityNotFoundException.class, () -> service.actualizarStock(404L, 1));
    }
}

