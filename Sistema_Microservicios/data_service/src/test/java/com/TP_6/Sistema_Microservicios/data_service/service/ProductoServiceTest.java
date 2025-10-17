package com.TP_6.Sistema_Microservicios.data_service.service;

import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.repository.ProductoRepository;
import com.TP_6.Sistema_Microservicios.data_service.service.impl.ProductoServiceImpl;
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
        service = new ProductoServiceImpl(productoRepo, categoriaRepo);
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
        p.setId(1L);
        when(productoRepo.findById(1L)).thenReturn(Optional.of(p));

        Producto out = service.buscarPorId(1L);
        assertEquals(1L, out.getId());
        verify(productoRepo).findById(1L);
    }

    @Test
    void buscarPorId_noExiste_lanzaIllegalArgument() {
        when(productoRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.buscarPorId(99L));
        verify(productoRepo).findById(99L);
    }

    @Test
    void buscarPorCategoria_ok() {
        when(productoRepo.findByCategoria_NombreIgnoreCase("Tecnología"))
                .thenReturn(List.of(new Producto(), new Producto(), new Producto()));

        assertEquals(3, service.buscarPorCategoria("Tecnología").size());
        verify(productoRepo).findByCategoria_NombreIgnoreCase("Tecnología");
    }

    @Test
    void buscarPorCategoria_nullOVacio_devuelveListaVacia() {
        assertTrue(service.buscarPorCategoria(null).isEmpty());
        assertTrue(service.buscarPorCategoria("   ").isEmpty());
        verify(productoRepo, never()).findByCategoria_NombreIgnoreCase(anyString());
    }

    @Test
    void guardar_asociaCategoriaPorNombre() {
        Producto p = new Producto();
        p.setNombre("Mouse");
        p.setPrecio(new BigDecimal("1000.00"));

        Categoria cat = new Categoria();
        cat.setId(7L);
        cat.setNombre("Tecnología");

        when(categoriaRepo.findByNombreIgnoreCase("Tecnología")).thenReturn(Optional.of(cat));
        when(productoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Producto out = service.guardar(p, "Tecnología");

        assertNotNull(out.getCategoria());
        assertEquals("Tecnología", out.getCategoria().getNombre());

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepo).save(captor.capture());
        assertEquals("Mouse", captor.getValue().getNombre());
        verify(categoriaRepo).findByNombreIgnoreCase("Tecnología");
    }

    @Test
    void guardar_sinCategoriaNombre_noBuscaNiCreaCategoria() {
        Producto p = new Producto();
        p.setNombre("Teclado");
        p.setPrecio(new BigDecimal("2500.00"));

        when(productoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Producto out = service.guardar(p, null);

        assertNull(out.getCategoria());
        verify(categoriaRepo, never()).findByNombreIgnoreCase(anyString());
        verify(productoRepo).save(any(Producto.class));
    }

    @Test
    void actualizar_modificaCamposBasicos_yPersiste() {
        Producto existente = new Producto();
        existente.setId(5L);
        existente.setNombre("Viejo");
        existente.setDescripcion("Desc vieja");
        existente.setPrecio(new BigDecimal("10"));

        when(productoRepo.findById(5L)).thenReturn(Optional.of(existente));
        when(productoRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Producto cambios = new Producto();
        cambios.setNombre("Nuevo");
        cambios.setDescripcion("Desc nueva");
        cambios.setPrecio(new BigDecimal("20"));

        Producto out = service.actualizar(5L, cambios);

        assertEquals("Nuevo", out.getNombre());
        assertEquals("Desc nueva", out.getDescripcion());
        assertEquals(new BigDecimal("20"), out.getPrecio());

        verify(productoRepo).findById(5L);
        verify(productoRepo).save(existente);
    }

    @Test
    void eliminar_ok() {
        when(productoRepo.existsById(3L)).thenReturn(true);
        service.eliminar(3L);
        verify(productoRepo).deleteById(3L);
    }

    @Test
    void eliminar_noExiste_lanzaIllegalArgument() {
        when(productoRepo.existsById(404L)).thenReturn(false);
        assertThrows(IllegalArgumentException.class, () -> service.eliminar(404L));
        verify(productoRepo, never()).deleteById(anyLong());
    }
}
