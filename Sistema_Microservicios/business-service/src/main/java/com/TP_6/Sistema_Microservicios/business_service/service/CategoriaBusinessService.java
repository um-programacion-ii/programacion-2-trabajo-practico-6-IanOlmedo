package com.TP_6.Sistema_Microservicios.business_service.service;

import com.TP_6.Sistema_Microservicios.business_service.client.DataServiceClient;
import com.TP_6.Sistema_Microservicios.business_service.dto.CategoriaDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaBusinessService {
    private final DataServiceClient dataClient;

    public CategoriaBusinessService(DataServiceClient dataClient) {
        this.dataClient = dataClient;
    }

    public List<CategoriaDTO> listar() {
        return dataClient.obtenerTodasLasCategorias();
    }
}
