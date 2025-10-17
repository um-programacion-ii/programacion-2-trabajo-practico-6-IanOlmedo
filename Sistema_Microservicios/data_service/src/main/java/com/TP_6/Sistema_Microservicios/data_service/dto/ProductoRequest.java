package com.TP_6.Sistema_Microservicios.data_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequest {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String categoriaNombre;
    private Integer stock;
}
