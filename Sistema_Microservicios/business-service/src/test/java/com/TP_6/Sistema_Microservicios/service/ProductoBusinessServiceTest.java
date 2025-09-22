package com.TP_6.Sistema_Microservicios.service;

import com.TP_6.Sistema_Microservicios.business_service.client.DataServiceClient;
import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoRequest;
import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoResponse;
import com.TP_6.Sistema_Microservicios.business_service.exception.BusinessException;
import com.TP_6.Sistema_Microservicios.business_service.service.ProductoBusinessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductoBusinessServiceTest {

    private DataServiceClient client;
    private ProductoBusinessService service;

    @BeforeEach
    void setUp() {
        client = mock(DataServiceClient.class);
        service = new ProductoBusinessService(client);
    }

    @Test
    void listar_ok() {
        ProductoResponse r = ProductoResponse.builder().id(1L).nombre("Mouse").build();
        when(client.obtenerTodosLosProductos()).thenReturn(List.of(r));

        List<ProductoResponse> out = service.listar();
        assertEquals(1, out.size());
        assertEquals("Mouse", out.get(0).getNombre());
        verify(client).obtenerTodosLosProductos();
    }

    @Test
    void porId_ok() {
        ProductoResponse r = ProductoResponse.builder().id(5L).nombre("X").build();
        when(client.obtenerProductoPorId(5L)).thenReturn(r);
        assertEquals(5L, service.porId(5L).getId());
    }

    @Test
    void crear_invalidoPrecio_lanza() {
        ProductoRequest req = ProductoRequest.builder()
                .nombre("P").precio(new BigDecimal("-1")).stock(1).categoriaNombre("C").build();
        assertThrows(BusinessException.class, () -> service.crear(req));
        verifyNoInteractions(client);
    }

    @Test
    void crear_ok_delegaAClient() {
        ProductoRequest req = ProductoRequest.builder()
                .nombre("P").precio(new BigDecimal("10")).stock(1).categoriaNombre("C").build();
        when(client.crearProducto(req)).thenReturn(ProductoResponse.builder().id(100L).nombre("P").build());

        ProductoResponse out = service.crear(req);
        assertEquals(100L, out.getId());
        verify(client).crearProducto(req);
    }

    @Test
    void actualizar_ok() {
        ProductoRequest req = ProductoRequest.builder()
                .nombre("P").precio(new BigDecimal("10")).stock(2).categoriaNombre("C").build();
        when(client.actualizarProducto(9L, req)).thenReturn(ProductoResponse.builder().id(9L).nombre("P").build());

        ProductoResponse out = service.actualizar(9L, req);
        assertEquals(9L, out.getId());
        verify(client).actualizarProducto(9L, req);
    }

    @Test
    void eliminar_ok() {
        service.eliminar(7L);
        verify(client).eliminarProducto(7L);
    }

    @Test
    void porCategoria_ok() {
        when(client.obtenerProductosPorCategoria("Tech")).thenReturn(List.of(
                ProductoResponse.builder().id(1L).nombre("A").build()
        ));
        assertEquals(1, service.porCategoria("Tech").size());
    }
}
