package com.clinicanuevomilenio.pabellones.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "comuna")
@Data
public class Comuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comuna")
    private Integer idComuna;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROVINCIA_id_provincia", nullable = false)
    private Provincia provincia;
}