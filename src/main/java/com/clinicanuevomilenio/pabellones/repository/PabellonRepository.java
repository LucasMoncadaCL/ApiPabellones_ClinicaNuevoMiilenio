package com.clinicanuevomilenio.pabellones.repository;

import com.clinicanuevomilenio.pabellones.models.Pabellon;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PabellonRepository extends JpaRepository<Pabellon, Integer>, JpaSpecificationExecutor<Pabellon> {
    List<Pabellon> findByTipo_Id(Integer tipoId);
    List<Pabellon> findByEstado_Id(Integer estadoId);
    List<Pabellon> findByEstado_IdAndTipo_Id(Integer estadoId, Integer tipoId);
}