package com.clinicanuevomilenio.pabellones.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas") // Nombre de la tabla en la base de datos
@Data
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne // Muchas reservas pueden ser para un pabellón
    @JoinColumn(name = "pabellon_id", nullable = false)
    private Pabellon pabellon;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFin;

    @Column
    private String nombrePaciente;

    @Column
    private String procedimiento;

}