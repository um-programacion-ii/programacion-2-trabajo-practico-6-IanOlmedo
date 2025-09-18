package com.TP_6.Sistema_Microservicios.data_service.repository;

import com.TP_6.Sistema_Microservicios.data_service.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    List<Inventario> findByCantidadLessThanEqual(Integer cantidad);
}
