package com.clinicanuevomilenio.pabellones.repository;

import com.clinicanuevomilenio.pabellones.models.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {
}