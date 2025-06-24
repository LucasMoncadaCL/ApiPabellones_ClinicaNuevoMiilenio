package com.clinicanuevomilenio.pabellones.dto;

import lombok.Data;

@Data
public class ComunaDTO {
    private Integer idComuna;
    private String nombre;
    private ProvinciaDTO provincia;
}