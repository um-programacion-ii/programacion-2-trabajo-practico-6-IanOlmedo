package com.TP_6.Sistema_Microservicios.business_service.client;

import com.TP_6.Sistema_Microservicios.business_service.BusinessServiceApplication;
import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoRequest;
import com.TP_6.Sistema_Microservicios.business_service.dto.ProductoResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = BusinessServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.cloud.compatibility-verifier.enabled=false" // evitar corte por versiones Cloud/Boot
)
class DataServiceClientTest {

    private static MockWebServer mockServer;

    @Autowired
    private DataServiceClient client;

    @BeforeAll
    static void start() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
    }

    @AfterAll
    static void stop() throws IOException {
        mockServer.shutdown();
    }

    // Inyecta la URL del mock en la propiedad que usa el @FeignClient
    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("data.service.url", () -> mockServer.url("/").toString());
    }

    @Test
    void obtenerTodosLosProductos_ok() throws Exception {
        String body = """
        [
          {"id":1,"nombre":"Mouse","descripcion":"Gamer","precio":9999.9,"categoria":"Tecnologia","stock":12,"stockBajo":false},
          {"id":2,"nombre":"Teclado","descripcion":"Mecanico","precio":19999.5,"categoria":"Tecnologia","stock":3,"stockBajo":true}
        ]
        """;
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type","application/json")
                .setBody(body));

        List<ProductoResponse> out = client.obtenerTodosLosProductos();

        assertThat(out).hasSize(2);
        assertThat(out.get(0).getNombre()).isEqualTo("Mouse");

        RecordedRequest req = mockServer.takeRequest();
        assertThat(req.getMethod()).isEqualTo("GET");
        assertThat(req.getPath()).isEqualTo("/data/productos");
    }

    @Test
    void crearProducto_enviaJsonYRecibe201() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setHeader("Content-Type","application/json")
                .setBody("""
                        {"id":10,"nombre":"Teclado","descripcion":"Mecanico","precio":20000.0,"categoria":"Tecnologia","stock":5,"stockBajo":false}
                        """));

        ProductoRequest req = ProductoRequest.builder()
                .nombre("Teclado")
                .descripcion("Mecanico")
                .precio(new BigDecimal("20000.0"))
                .categoriaNombre("Tecnologia")
                .stock(5)
                .build();

        ProductoResponse resp = client.crearProducto(req);
        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getNombre()).isEqualTo("Teclado");

        RecordedRequest http = mockServer.takeRequest();
        assertThat(http.getMethod()).isEqualTo("POST");
        assertThat(http.getPath()).isEqualTo("/data/productos");
        assertThat(http.getHeader("Content-Type")).contains("application/json");

        String sent = http.getBody().readString(StandardCharsets.UTF_8);
        assertThat(sent).contains("\"nombre\":\"Teclado\"");
        assertThat(sent).contains("\"precio\":20000.0");
        assertThat(sent).contains("\"categoriaNombre\":\"Tecnologia\"");
    }
}
