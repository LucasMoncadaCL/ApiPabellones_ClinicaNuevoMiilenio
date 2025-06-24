package com.clinicanuevomilenio.pabellones.dto;

import lombok.Data;

@Data
public class SedeDTO {
    private Integer id;
    private String nombre;
    private ComunaDTO comuna;
}