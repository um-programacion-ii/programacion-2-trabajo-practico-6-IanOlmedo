package com.TP_6.Sistema_Microservicios.business_service.client;

import com.TP_6.Sistema_Microservicios.business_service.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(
        name = "data-service",
        url = "${data.service.url}",
        configuration = FeignConfig.class
)
public interface DataServiceClient {

    // PRODUCTOS
    @GetMapping("/data/productos")
    List<ProductoResponse> obtenerTodosLosProductos();

    @GetMapping("/data/productos/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable Long id);

    @PostMapping("/data/productos")
    ProductoResponse crearProducto(@RequestBody ProductoRequest request);

    @PutMapping("/data/productos/{id}")
    ProductoResponse actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequest request);

    @DeleteMapping("/data/productos/{id}")
    void eliminarProducto(@PathVariable Long id);

    @GetMapping("/data/productos/categoria/{nombre}")
    List<ProductoResponse> obtenerProductosPorCategoria(@PathVariable String nombre);

    // CATEGORIAS
    @GetMapping("/data/categorias")
    List<CategoriaDTO> obtenerTodasLasCategorias();

    // INVENTARIO
    @GetMapping("/data/inventario/stock-bajo")
    List<InventarioDTO> obtenerProductosConStockBajo();
}
