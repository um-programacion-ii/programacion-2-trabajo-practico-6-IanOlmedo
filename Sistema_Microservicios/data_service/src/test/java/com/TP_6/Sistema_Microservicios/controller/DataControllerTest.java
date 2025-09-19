package com.TP_6.Sistema_Microservicios.controller;


import com.TP_6.Sistema_Microservicios.data_service.controller.DataController;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.service.CategoriaService;
import com.TP_6.Sistema_Microservicios.data_service.service.InventarioService;
import com.TP_6.Sistema_Microservicios.data_service.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DataController.class)
class DataControllerTest {

    @Autowired MockMvc mvc;

    @MockBean ProductoService productoService;
    @MockBean CategoriaService categoriaService;
    @MockBean InventarioService inventarioService;

    @Test
    void listarProductos_ok() throws Exception {
        Producto p = new Producto();
        p.setId(1L); p.setNombre("Mouse"); p.setPrecio(new BigDecimal("1000.00"));
        when(productoService.obtenerTodos()).thenReturn(List.of(p));

        mvc.perform(get("/data/productos").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("Mouse"));
    }
}