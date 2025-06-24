package com.clinicanuevomilenio.pabellones.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "pabellon_imagen")
@Data
public class PabellonImagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imagen_id")
    private Integer id;

    @Column(name = "ruta_archivo", nullable = false)
    private String rutaArchivo;

    @Column(name = "nombre_archivo", nullable = false, length = 100)
    private String nombreArchivo;

    @Column(name = "tipo_mime", nullable = false, length = 50)
    private String tipoMime;

    @Column(name = "tamano_bytes",length = 11)
    private int tamanoBytes;

    @Column(name = "es_principal", nullable = false, length = 1)
    private int esPrincipal;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDate fechaSubida;

    @Column(name = "orden",length = 11)
    private int orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PABELLON_pabellon_id", nullable = false)
    private Pabellon pabellon;
}