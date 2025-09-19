package com.TP_6.Sistema_Microservicios.service;

import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.service.CategoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {
    @Test
    void buscarPorNombre_ok() {
        CategoriaRepository repo = mock(CategoriaRepository.class);
        CategoriaService service = new CategoriaService(repo);
        Categoria c = new Categoria(); c.setNombre("Tecno");
        when(repo.findByNombreIgnoreCase("Tecno")).thenReturn(Optional.of(c));
        assertEquals("Tecno", service.buscarPorNombre("Tecno").getNombre());
    }

    @Test
    void buscarPorNombre_noExiste() {
        CategoriaRepository repo = mock(CategoriaRepository.class);
        when(repo.findByNombreIgnoreCase("X")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> new CategoriaService(repo).buscarPorNombre("X"));
    }
}