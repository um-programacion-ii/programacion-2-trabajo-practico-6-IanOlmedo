package com.TP_6.Sistema_Microservicios.business_service.service;

import com.TP_6.Sistema_Microservicios.business_service.client.DataServiceClient;
import com.TP_6.Sistema_Microservicios.business_service.dto.InventarioDTO;
import com.TP_6.Sistema_Microservicios.business_service.service.InventarioBusinessService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InventarioBusinessServiceTest {

    @Test
    void conStockBajo_ok() {
        DataServiceClient client = mock(DataServiceClient.class);
        when(client.obtenerProductosConStockBajo()).thenReturn(List.of(
                new InventarioDTO(1L, 10L, 2, 5),
                new InventarioDTO(2L, 11L, 1, 5)
        ));

        InventarioBusinessService service = new InventarioBusinessService(client);
        assertEquals(2, service.conStockBajo().size());
        verify(client).obtenerProductosConStockBajo();
    }
}
