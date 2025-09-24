package com.TP_6.Sistema_Microservicios.business_service.service;

import com.TP_6.Sistema_Microservicios.business_service.client.DataServiceClient;
import com.TP_6.Sistema_Microservicios.business_service.dto.InventarioDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioBusinessService {
    private final DataServiceClient dataClient;

    public InventarioBusinessService(DataServiceClient dataClient) {
        this.dataClient = dataClient;
    }

    public List<InventarioDTO> conStockBajo() {
        return dataClient.obtenerProductosConStockBajo();
    }
}
