package com.TP_6.Sistema_Microservicios.data_service.repository;

import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria_NombreIgnoreCase(String nombre);
}
