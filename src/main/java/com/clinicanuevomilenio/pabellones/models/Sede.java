package com.clinicanuevomilenio.pabellones.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "sede") //
@Data
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sede")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Column(name = "telefono", nullable = false, length = 11)
    private int telefono;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "horario_apertura", nullable = false)
    private LocalDate horarioApertura;

    @Column(name = "hora_cierre", nullable = false)
    private LocalDate horarioCierre;

    @Column(name = "activo", nullable = false, length = 1)
    private int activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMUNA_id_comuna", nullable = false)
    private Comuna comuna;
}