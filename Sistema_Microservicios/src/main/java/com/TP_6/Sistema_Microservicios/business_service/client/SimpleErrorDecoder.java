package com.TP_6.Sistema_Microservicios.business_service.client;

import com.TP_6.Sistema_Microservicios.business_service.exception.*;
import feign.Response;
import feign.codec.ErrorDecoder;

public class SimpleErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        if (status == 404) return new NotFoundException("Recurso no encontrado");
        if (status >= 400 && status < 500) return new BusinessException("Error de cliente (" + status + ")");
        if (status >= 500) return new MicroserviceCommunicationException("Error del data-service (" + status + ")");
        return new RuntimeException("Falla desconocida (" + status + ")");
    }
}
