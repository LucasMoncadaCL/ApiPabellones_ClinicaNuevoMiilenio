package com.clinicanuevomilenio.pabellones.repository;

import com.clinicanuevomilenio.pabellones.models.PabellonImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PabellonImagenRepository extends JpaRepository<PabellonImagen, Integer> {
}
