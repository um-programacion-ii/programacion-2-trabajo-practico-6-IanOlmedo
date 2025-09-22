package com.TP_6.Sistema_Microservicios.service;

import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.service.CategoriaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoriaServiceTest {

    @Test
    void obtenerTodas_ok() {
        CategoriaRepository repo = mock(CategoriaRepository.class);
        when(repo.findAll()).thenReturn(List.of(new Categoria(), new Categoria()));
        CategoriaService service = new CategoriaService(repo);

        assertEquals(2, service.obtenerTodas().size());
        verify(repo).findAll();
    }

    @Test
    void guardar_ok() {
        CategoriaRepository repo = mock(CategoriaRepository.class);
        CategoriaService service = new CategoriaService(repo);
        Categoria c = new Categoria(); c.setNombre("Tech");
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        Categoria out = service.guardar(c);
        assertEquals("Tech", out.getNombre());
        verify(repo).save(c);
    }

    @Test
    void buscarPorNombre_encontrada() {
        CategoriaRepository repo = mock(CategoriaRepository.class);
        Categoria c = new Categoria(); c.setNombre("Tech");
        when(repo.findByNombreIgnoreCase("Tech")).thenReturn(Optional.of(c));
        CategoriaService service = new CategoriaService(repo);

        assertEquals("Tech", service.buscarPorNombre("Tech").getNombre());
    }

    @Test
    void buscarPorNombre_noExiste_lanza() {
        CategoriaRepository repo = mock(CategoriaRepository.class);
        when(repo.findByNombreIgnoreCase("X")).thenReturn(Optional.empty());
        CategoriaService service = new CategoriaService(repo);

        assertThrows(EntityNotFoundException.class, () -> service.buscarPorNombre("X"));
    }
}
