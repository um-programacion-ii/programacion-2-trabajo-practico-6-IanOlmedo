package com.TP_6.Sistema_Microservicios.business_service.integration;

import com.TP_6.Sistema_Microservicios.business_service.BusinessServiceApplication;
import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoResponse;
import com.TP_6.Sistema_Microservicios.business_service.service.ProductoBusinessService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = BusinessServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.cloud.compatibility-verifier.enabled=false" // <- desactiva el check SOLO en este test
)
class MicroservicesCommunicationTest {

    private static MockWebServer mockDataService;

    @Autowired
    private ProductoBusinessService productoBusinessService;

    @BeforeAll
    static void startServer() throws IOException {
        mockDataService = new MockWebServer();
        mockDataService.start(); // puerto aleatorio
    }

    @AfterAll
    static void shutdown() throws IOException {
        mockDataService.shutdown();
    }

    // Inyecta dinámicamente la URL base para Feign: data.service.url
    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("data.service.url", () -> mockDataService.url("/").toString());
    }

    @Test
    void listarProductos_llamaAlDataServiceYMapeaRespuesta() throws Exception {
        //devuelve json
        String json = """
        [
          {
            "id": 1,
            "nombre": "Mouse",
            "descripcion": "Gamer",
            "precio": 9999.90,
            "categoria": "Tecnologia",
            "stock": 12,
            "stockBajo": false
          },
          {
            "id": 2,
            "nombre": "Teclado",
            "descripcion": "Mecánico",
            "precio": 19999.50,
            "categoria": "Tecnologia",
            "stock": 3,
            "stockBajo": true
          }
        ]
        """;

        mockDataService.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader("Content-Type", "application/json")
                        .setBody(json)
        );

        List<ProductoResponse> productos = productoBusinessService.listar();

        // verificaciones de negocio
        assertThat(productos).hasSize(2);
        assertThat(productos.get(0).getNombre()).isEqualTo("Mouse");
        assertThat(productos.get(1).getStock()).isEqualTo(3);

        //verificamos que Feign haya llamado correctamente al endpoint remoto
        RecordedRequest req = mockDataService.takeRequest();
        assertThat(req.getMethod()).isEqualTo("GET");
        assertThat(req.getPath()).isEqualTo("/data/productos");
    }
}
