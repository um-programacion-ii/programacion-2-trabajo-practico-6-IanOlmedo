package com.TP_6.Sistema_Microservicios.business_service.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoRequest {
    @NotBlank private String nombre;
    private String descripcion;
    @NotNull @DecimalMin(value="0.01") private BigDecimal precio;
    @NotBlank private String categoriaNombre;
    @NotNull @Min(0) private Integer stock;
}