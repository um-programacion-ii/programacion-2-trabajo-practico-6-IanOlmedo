package com.TP_6.Sistema_Microservicios.service;

import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.repository.ProductoRepository;
import com.TP_6.Sistema_Microservicios.data_service.service.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    private ProductoRepository productoRepo;
    private CategoriaRepository categoriaRepo;
    private ProductoService service;

    @BeforeEach
    void setUp() {
        productoRepo = mock(ProductoRepository.class);
        categoriaRepo = mock(CategoriaRepository.class);
        service = new ProductoService(productoRepo, categoriaRepo);
    }

    @Test
    void obtenerTodos_ok() {
        when(productoRepo.findAll()).thenReturn(List.of(new Producto(), new Producto()));
        assertEquals(2, service.obtenerTodos().size());
        verify(productoRepo).findAll();
    }

    @Test
    void buscarPorId_encontrado() {
        Producto p = new Producto();
        p.setId(10L);
        when(productoRepo.findById(10L)).thenReturn(Optional.of(p));
        assertEquals(10L, service.buscarPorId(10L).getId());
    }

    @Test
    void buscarPorId_noExiste_lanza() {
        when(productoRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void guardar_asociaCategoriaPorNombre() {
        Producto p = new Producto();
        p.setNombre("Mouse");
        p.setPrecio(new BigDecimal("1000.00"));

        Categoria cat = new Categoria();
        cat.setId(1L);
        cat.setNombre("Tecnología");

        when(categoriaRepo.findByNombreIgnoreCase("Tecnología")).thenReturn(Optional.of(cat));
        when(productoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Producto out = service.guardar(p, "Tecnología");
        assertNotNull(out.getCategoria());
        assertEquals("Tecnología", out.getCategoria().getNombre());

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepo).save(captor.capture());
        assertEquals("Mouse", captor.getValue().getNombre());
    }
}