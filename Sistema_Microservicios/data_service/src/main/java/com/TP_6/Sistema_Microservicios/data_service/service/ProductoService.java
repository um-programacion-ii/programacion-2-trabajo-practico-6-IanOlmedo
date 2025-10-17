package com.TP_6.Sistema_Microservicios.data_service.service;

import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;

import java.util.List;

public interface ProductoService {

    List<Producto> obtenerTodos();

    Producto buscarPorId(Long id);

    /**
     * Guarda un producto asignándolo (o creando si no existe) a la categoría cuyo nombre se pasa.
     * Se respeta el inventario ya seteado en el Producto (si viene).
     */
    Producto guardar(Producto producto, String categoriaNombre);

    /**
     * Actualiza un producto ya existente (merge de campos).
     * Se asume que si llega con categoría/inventario en el objeto, ya vienen seteados.
     */
    Producto actualizar(Long id, Producto productoActualizado);

    void eliminar(Long id);

    List<Producto> buscarPorCategoria(String nombreCategoria);
}
