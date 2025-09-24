package com.TP_6.Sistema_Microservicios.business_service.controller;

import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoRequest;
import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoResponse;
import com.TP_6.Sistema_Microservicios.business_service.service.ProductoBusinessService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BusinessController.class)
class BusinessControllerWebTest {

    @Autowired MockMvc mvc;

    @MockBean ProductoBusinessService service;

    @Test
    void GET_listar_ok() throws Exception {
        Mockito.when(service.listar()).thenReturn(List.of(
                ProductoResponse.builder().id(1L).nombre("Mouse").precio(new BigDecimal("1000.00")).build()
        ));

        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Mouse"));
    }

    @Test
    void GET_porId_ok() throws Exception {
        Mockito.when(service.porId(5L))
                .thenReturn(ProductoResponse.builder().id(5L).nombre("Teclado").build());

        mvc.perform(get("/api/productos/{id}", 5))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.nombre").value("Teclado"));
    }

    @Test
    void GET_porCategoria_ok() throws Exception {
        Mockito.when(service.porCategoria("Tech"))
                .thenReturn(List.of(ProductoResponse.builder().id(2L).nombre("Headset").build()));

        mvc.perform(get("/api/productos/categoria/{nombre}", "Tech"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    void POST_crear_ok() throws Exception {
        ProductoResponse resp = ProductoResponse.builder().id(100L).nombre("Nuevo").build();
        Mockito.when(service.crear(any(ProductoRequest.class))).thenReturn(resp);

        String body = """
        {"nombre":"Nuevo","descripcion":"d","precio":10.0,"categoriaNombre":"Tech","stock":1}
        """;

        mvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre").value("Nuevo"));
    }

    @Test
    void PUT_actualizar_ok() throws Exception {
        ProductoResponse resp = ProductoResponse.builder().id(7L).nombre("Editado").build();
        Mockito.when(service.actualizar(eq(7L), any(ProductoRequest.class))).thenReturn(resp);

        String body = """
        {"nombre":"Editado","descripcion":"d","precio":99.9,"categoriaNombre":"Tech","stock":3}
        """;

        mvc.perform(put("/api/productos/{id}", 7)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Editado"));
    }

    @Test
    void DELETE_eliminar_noContent() throws Exception {
        mvc.perform(delete("/api/productos/{id}", 8))
                .andExpect(status().isNoContent());
    }
}
