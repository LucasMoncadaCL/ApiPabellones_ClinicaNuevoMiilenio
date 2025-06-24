package com.clinicanuevomilenio.pabellones.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pabellon")
@Data
public class Pabellon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pabellon_id")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Column(name = "edificio", nullable = false, length = 50)
    private String edificio;

    @Column(name = "piso", nullable = false)
    private Integer piso;

    @Column(name = "referencia", nullable = false, length = 50)
    private String referencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", nullable = false)
    private EstadoPabellon estado;

    @Column(name = "caracteristicas", length = 200)
    private String caracteristicas;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDate fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TIPO_PABELLON_id_tipo_pabellon", nullable = false)
    private TipoPabellon tipoPabellon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEDE_id_sede", nullable = false)
    private Sede sede;

    @OneToMany(mappedBy = "pabellon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PabellonImagen> imagenes;
}
