package com.TP_6.Sistema_Microservicios.business_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
}
