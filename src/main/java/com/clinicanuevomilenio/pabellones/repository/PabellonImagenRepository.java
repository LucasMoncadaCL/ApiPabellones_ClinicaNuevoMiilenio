package com.clinicanuevomilenio.pabellones.repository;

import com.clinicanuevomilenio.pabellones.models.PabellonImagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface PabellonImagenRepository extends JpaRepository<PabellonImagen, Integer> {
    Optional<PabellonImagen> findByPabellonIdAndEsPrincipal(Integer pabellonId, int esPrincipal);

@Repository
public interface PabellonImagenRepository extends JpaRepository<PabellonImagen, Integer> {

}
