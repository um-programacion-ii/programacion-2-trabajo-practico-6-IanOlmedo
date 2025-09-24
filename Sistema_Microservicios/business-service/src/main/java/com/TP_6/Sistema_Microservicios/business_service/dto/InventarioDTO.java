package com.TP_6.Sistema_Microservicios.business_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioDTO {
    private Long id;
    private Long productoId;
    private Integer cantidad;
    private Integer stockMinimo;
}