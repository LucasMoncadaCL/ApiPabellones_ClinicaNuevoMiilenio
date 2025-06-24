package com.clinicanuevomilenio.pabellones.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "provincia")
@Data
public class Provincia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_provincia")
    private Integer idProvincia;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
}