package com.clinicanuevomilenio.pabellones.repository;

import com.clinicanuevomilenio.pabellones.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {
}
