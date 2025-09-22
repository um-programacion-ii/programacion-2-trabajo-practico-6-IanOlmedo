package com.TP_6.Sistema_Microservicios.service;

import com.TP_6.Sistema_Microservicios.business_service.client.DataServiceClient;
import com.TP_6.Sistema_Microservicios.business_service.dto.CategoriaDTO;
import com.TP_6.Sistema_Microservicios.business_service.service.CategoriaBusinessService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CategoriaBusinessServiceTest {

    @Test
    void listar_ok() {
        DataServiceClient client = mock(DataServiceClient.class);
        when(client.obtenerTodasLasCategorias()).thenReturn(List.of(
                new CategoriaDTO(1L, "Tech", "t"), new CategoriaDTO(2L, "Hogar", "h")
        ));
        CategoriaBusinessService service = new CategoriaBusinessService(client);

        assertEquals(2, service.listar().size());
        verify(client).obtenerTodasLasCategorias();
    }
}
