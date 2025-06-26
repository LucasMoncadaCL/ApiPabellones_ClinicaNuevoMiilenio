package com.clinicanuevomilenio.pabellones.repository;

import com.clinicanuevomilenio.pabellones.models.Pabellon;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PabellonRepository extends JpaRepository<Pabellon, Integer> {
    List<Pabellon> findByTipo_Id(Integer tipoId);
    List<Pabellon> findByEstado_Id(Integer estadoId);
    List<Pabellon> findByEstado_IdAndTipo_Id(Integer estadoId, Integer tipoId);

    @Query("SELECT p FROM Pabellon p WHERE " +
            "(:estadoId IS NULL OR p.estado.id = :estadoId) AND " +
            "(:tipoId IS NULL OR p.tipo.id = :tipoId) AND " +
            "NOT EXISTS (SELECT r FROM Reserva r WHERE r.pabellon.id = p.id AND r.fecha = :fecha)")
    List<Pabellon> findPabellonesDisponibles(
            @Param("fecha") LocalDate fecha,
            @Param("estadoId") Integer estadoId,
            @Param("tipoId") Integer tipoId);
}