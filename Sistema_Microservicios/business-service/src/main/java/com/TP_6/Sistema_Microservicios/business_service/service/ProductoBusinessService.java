package com.TP_6.Sistema_Microservicios.business_service.service;

import com.TP_6.Sistema_Microservicios.business_service.client.DataServiceClient;
import com.TP_6.Sistema_Microservicios.business_service.dto.*;
import com.TP_6.Sistema_Microservicios.business_service.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductoBusinessService {

    private final DataServiceClient dataClient;

    public ProductoBusinessService(DataServiceClient dataClient) {
        this.dataClient = dataClient;
    }

    public List<ProductoResponse> listar() {
        return dataClient.obtenerTodosLosProductos();
    }

    public ProductoResponse porId(Long id) {
        return dataClient.obtenerProductoPorId(id);
    }

    public ProductoResponse crear(ProductoRequest req) {
        validar(req);
        return dataClient.crearProducto(req);
    }

    public ProductoResponse actualizar(Long id, ProductoRequest req) {
        validar(req);
        return dataClient.actualizarProducto(id, req);
    }

    public void eliminar(Long id) {
        dataClient.eliminarProducto(id);
    }

    public List<ProductoResponse> porCategoria(String categoria) {
        return dataClient.obtenerProductosPorCategoria(categoria);
    }

    private void validar(ProductoRequest req) {
        if (req.getPrecio() == null || req.getPrecio().compareTo(BigDecimal.ZERO) <= 0)
            throw new BusinessException("El precio debe ser mayor a cero");
        if (req.getStock() == null || req.getStock() < 0)
            throw new BusinessException("El stock no puede ser negativo");
    }
}
