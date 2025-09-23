package com.TP_6.Sistema_Microservicios.controller;

import com.TP_6.Sistema_Microservicios.SistemaMicroserviciosApplication;
import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Inventario;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = com.TP_6.Sistema_Microservicios.SistemaMicroserviciosApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)

@AutoConfigureMockMvc
@ActiveProfiles("dev")   // usa application-dev.yml (H2 en memoria)
@Transactional
class DataControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ProductoRepository productoRepo;
    @Autowired CategoriaRepository categoriaRepo;

    private Long productoId;

    @BeforeEach
    void seed() {
        categoriaRepo.deleteAll();
        productoRepo.deleteAll();

        Categoria cat = new Categoria();
        cat.setNombre("Tecnología");
        cat.setDescripcion("Cat de prueba");
        categoriaRepo.save(cat);

        Producto p = new Producto();
        p.setNombre("Mouse Gamer");
        p.setDescripcion("RGB");
        p.setPrecio(new BigDecimal("9999.90"));
        p.setCategoria(cat);

        Inventario inv = new Inventario();
        inv.setCantidad(10);
        inv.setStockMinimo(3);
        inv.setFechaActualizacion(LocalDateTime.now());
        p.setInventario(inv);

        productoId = productoRepo.save(p).getId();
    }

    @Test
    void GET_productos_ok() throws Exception {
        mvc.perform(get("/data/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nombre", not(emptyString())));
    }

    @Test
    void GET_productoPorId_ok() throws Exception {
        mvc.perform(get("/data/productos/{id}", productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoId))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"));
    }

    @Test
    void GET_productoPorId_noExiste_404() throws Exception {
        mvc.perform(get("/data/productos/{id}", 99999))
                .andExpect(status().isNotFound());
    }

    @Test
    void GET_porCategoria_ok() throws Exception {
        mvc.perform(get("/data/productos/categoria/{nombre}", "Tecnología"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categoria.nombre").value("Tecnología"));
    }

    @Test
    void POST_crearProducto_ok() throws Exception {
        String body = """
        {
          "nombre": "Teclado Mecánico",
          "descripcion": "Blue Switch",
          "precio": 19999.50
        }
        """;

        mvc.perform(post("/data/productos")
                        .queryParam("categoria", "Tecnología")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre").value("Teclado Mecánico"))
                .andExpect(jsonPath("$.categoria.nombre").value("Tecnología"));
    }

    @Test
    void DELETE_eliminarProducto_noContent() throws Exception {
        mvc.perform(delete("/data/productos/{id}", productoId))
                .andExpect(status().isNoContent());
    }
}
