package com.TP_6.Sistema_Microservicios.data_service.client;

import com.TP_6.Sistema_Microservicios.SistemaMicroserviciosApplication;
import com.TP_6.Sistema_Microservicios.data_service.dto.ProductoRequest;
import com.TP_6.Sistema_Microservicios.data_service.dto.ProductoResponse;
import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Inventario;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = SistemaMicroserviciosApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("dev") // H2 en memoria
class DataServiceClientTest {

    @Autowired TestRestTemplate client;
    @Autowired ProductoRepository productoRepo;
    @Autowired CategoriaRepository categoriaRepo;

    private Long productoId;
    private String categoriaNombre;

    @BeforeEach
    void seed() {
        // limpieza manual ya que NO usamos @Transactional
        productoRepo.deleteAll();
        categoriaRepo.deleteAll();

        categoriaNombre = "Tecnología Test " + UUID.randomUUID();

        Categoria cat = new Categoria();
        cat.setNombre(categoriaNombre);
        cat.setDescripcion("Cat de prueba");
        categoriaRepo.saveAndFlush(cat); // <-- FLUSH para visibilidad fuera de la tx del test

        Producto p = new Producto();
        p.setNombre("Mouse Gamer");
        p.setDescripcion("RGB");
        p.setPrecio(new BigDecimal("9999.90"));
        p.setCategoria(cat);

        Inventario inv = new Inventario();
        inv.setCantidad(10);
        inv.setStockMinimo(5);
        inv.setFechaActualizacion(LocalDateTime.now());

        p.setInventario(inv);
        inv.setProducto(p);

        productoId = productoRepo.saveAndFlush(p).getId(); // <-- FLUSH
    }

    @Test
    void getProductos_ok() {
        ResponseEntity<ProductoResponse[]> resp =
                client.getForEntity("/data/productos", ProductoResponse[].class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().length).isGreaterThanOrEqualTo(1);

        ProductoResponse primero = resp.getBody()[0];
        // contrato plano
        assertThat(primero.getNombre()).isNotBlank();
        assertThat(primero.getStock()).isNotNull();
        assertThat(primero.getStockBajo()).isNotNull();
    }

    @Test
    void getProductoPorId_ok() {
        ResponseEntity<ProductoResponse> resp =
                client.getForEntity("/data/productos/{id}", ProductoResponse.class, productoId);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductoResponse dto = resp.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(productoId);
        assertThat(dto.getNombre()).isEqualTo("Mouse Gamer");
        assertThat(dto.getCategoriaNombre()).isEqualTo(categoriaNombre);
        assertThat(dto.getStock()).isGreaterThanOrEqualTo(0);
        assertThat(dto.getStockBajo()).isFalse(); // 10 > 5
    }

    @Test
    void getProductoPorId_noExiste_404() {
        ResponseEntity<Map> resp =
                client.getForEntity("/data/productos/{id}", Map.class, 999999L);

        // requiere el GlobalExceptionHandler mapeando a 404 (ya te lo pasé)
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody()).containsKey("error");
    }

    @Test
    void getPorCategoria_ok() {
        ResponseEntity<ProductoResponse[]> resp =
                client.getForEntity("/data/productos/categoria/{nombre}",
                        ProductoResponse[].class, categoriaNombre);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().length).isGreaterThanOrEqualTo(1);
        assertThat(resp.getBody()[0].getCategoriaNombre()).isEqualTo(categoriaNombre);
    }

    @Test
    void postCrearProducto_ok() {
        ProductoRequest req = ProductoRequest.builder()
                .nombre("Teclado Mecánico")
                .descripcion("Blue Switch")
                .precio(new BigDecimal("19999.50"))
                .categoriaNombre(categoriaNombre) // usar la ya existente evita UNIQUE violation
                .stock(7)
                .build();

        ResponseEntity<ProductoResponse> resp =
                client.postForEntity("/data/productos", req, ProductoResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ProductoResponse dto = resp.getBody();
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isNotNull();
        assertThat(dto.getNombre()).isEqualTo("Teclado Mecánico");
        assertThat(dto.getCategoriaNombre()).isEqualTo(categoriaNombre);
        assertThat(dto.getStock()).isEqualTo(7);
        assertThat(dto.getStockBajo()).isFalse(); // 7 > 5
    }

    @Test
    void deleteProducto_noContent() {
        ResponseEntity<Void> resp =
                client.exchange("/data/productos/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, productoId);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
