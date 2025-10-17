package com.TP_6.Sistema_Microservicios.data_service.controller;

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
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = SistemaMicroserviciosApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@ActiveProfiles("dev") // H2 en memoria
@Transactional
class DataControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ProductoRepository productoRepo;
    @Autowired CategoriaRepository categoriaRepo;

    private Long productoId;
    private String categoriaNombre; // ← usamos un nombre único por ejecución

    @BeforeEach
    void seed() {
        // Limpieza defensiva (por si hay data precargada por data.sql/runner)
        productoRepo.deleteAll();
        categoriaRepo.deleteAll();

        // Nombre único para evitar choques con el índice único
        categoriaNombre = "Tecnología Test " + UUID.randomUUID();

        Categoria cat = new Categoria();
        cat.setNombre(categoriaNombre);
        cat.setDescripcion("Cat de prueba");
        categoriaRepo.save(cat);

        Producto p = new Producto();
        p.setNombre("Mouse Gamer");
        p.setDescripcion("RGB");
        p.setPrecio(new BigDecimal("9999.90"));
        p.setCategoria(cat);

        Inventario inv = new Inventario();
        inv.setCantidad(10);           // > stockMinimo → stockBajo = false
        inv.setStockMinimo(5);         // el controller usa este mínimo para calcular stockBajo
        inv.setFechaActualizacion(LocalDateTime.now());

        p.setInventario(inv);
        inv.setProducto(p);

        productoId = productoRepo.save(p).getId();
    }

    @Test
    void GET_productos_ok() throws Exception {
        mvc.perform(get("/data/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].nombre", not(emptyString())))
                .andExpect(jsonPath("$[0].categoriaNombre", anyOf(nullValue(), not(emptyString()))))
                .andExpect(jsonPath("$[0].stock", anyOf(nullValue(), greaterThanOrEqualTo(0))))
                .andExpect(jsonPath("$[0].stockBajo", anyOf(is(true), is(false))));
    }

    @Test
    void GET_productoPorId_ok() throws Exception {
        mvc.perform(get("/data/productos/{id}", productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productoId))
                .andExpect(jsonPath("$.nombre").value("Mouse Gamer"))
                .andExpect(jsonPath("$.categoriaNombre").value(categoriaNombre))
                .andExpect(jsonPath("$.stock", greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.stockBajo").value(false)); // 10 > 5
    }

    @Test
    void GET_productoPorId_noExiste_404() throws Exception {
        mvc.perform(get("/data/productos/{id}", 999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void GET_porCategoria_ok() throws Exception {
        mvc.perform(get("/data/productos/categoria/{nombre}", categoriaNombre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].categoriaNombre").value(categoriaNombre))
                .andExpect(jsonPath("$[0].stockBajo", anyOf(is(true), is(false))));
    }

    @Test
    void POST_crearProducto_ok() throws Exception {
        // Usamos la misma categoría creada en el seed (evita duplicados)
        String body = String.format("""
        {
          "nombre": "Teclado Mecánico",
          "descripcion": "Blue Switch",
          "precio": 19999.50,
          "categoriaNombre": "%s",
          "stock": 7
        }
        """, categoriaNombre);

        mvc.perform(post("/data/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre").value("Teclado Mecánico"))
                .andExpect(jsonPath("$.categoriaNombre").value(categoriaNombre))
                .andExpect(jsonPath("$.stock").value(7))
                .andExpect(jsonPath("$.stockBajo").value(false)); // 7 > 5
    }

    @Test
    void DELETE_eliminarProducto_noContent() throws Exception {
        mvc.perform(delete("/data/productos/{id}", productoId))
                .andExpect(status().isNoContent());
    }
}
