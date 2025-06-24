package com.clinicanuevomilenio.pabellones.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tipo_pabellon")
@Data
public class TipoPabellon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_pabellon")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
}