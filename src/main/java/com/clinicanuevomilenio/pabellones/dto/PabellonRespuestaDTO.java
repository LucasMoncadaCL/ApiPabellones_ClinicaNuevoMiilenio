package com.clinicanuevomilenio.pabellones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PabellonRespuestaDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private int capacidad;
    private String edificio;
    private int piso;
    private String referencia;
    private String caracteristicas;

    private EstadoPabellonDTO estado;
    private TipoPabellonDTO tipoPabellon;
    private SedeDTO sede;

    private List<PabellonImagenDTO> imagenes;
}