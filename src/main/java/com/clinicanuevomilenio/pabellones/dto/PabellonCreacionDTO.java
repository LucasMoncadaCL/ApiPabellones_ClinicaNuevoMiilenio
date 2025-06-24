package com.clinicanuevomilenio.pabellones.dto;

import lombok.Data;

@Data
public class PabellonCreacionDTO {
    private String nombre;
    private String descripcion;
    private int capacidad;
    private String edificio;
    private int piso;
    private String referencia;
    private String caracteristicas;

    private int estadoId;
    private int tipoPabellonId;
    private int sedeId;
}