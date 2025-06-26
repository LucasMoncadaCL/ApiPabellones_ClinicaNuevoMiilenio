package com.clinicanuevomilenio.pabellones.services;

import com.clinicanuevomilenio.pabellones.dto.*;
import com.clinicanuevomilenio.pabellones.models.*;
import com.clinicanuevomilenio.pabellones.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PabellonService {

    @Autowired private PabellonRepository pabellonRepository;
    @Autowired private SedeRepository sedeRepository;
    @Autowired private TipoPabellonRepository tipoPabellonRepository;
    @Autowired private EstadoPabellonRepository estadoPabellonRepository;
    @Autowired private FileSystemStorageService storageService;
    @Autowired private PabellonImagenRepository pabellonImagenRepository;

    @Transactional
    public PabellonRespuestaDTO crearPabellon(PabellonCreacionDTO dto) {
        Sede sede = sedeRepository.findById(dto.getSedeId())
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada con ID: " + dto.getSedeId()));

        TipoPabellon tipo = tipoPabellonRepository.findById(dto.getTipoPabellonId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de Pabellón no encontrado con ID: " + dto.getTipoPabellonId()));

        EstadoPabellon estado = estadoPabellonRepository.findById(dto.getEstadoId())
                .orElseThrow(() -> new EntityNotFoundException("Estado de Pabellón no encontrado con ID: " + dto.getEstadoId()));

        Pabellon nuevoPabellon = new Pabellon();
        nuevoPabellon.setNombre(dto.getNombre());
        nuevoPabellon.setDescripcion(dto.getDescripcion());
        nuevoPabellon.setCapacidad(dto.getCapacidad());
        nuevoPabellon.setEdificio(dto.getEdificio());
        nuevoPabellon.setPiso(dto.getPiso());
        nuevoPabellon.setReferencia(dto.getReferencia());
        nuevoPabellon.setCaracteristicas(dto.getCaracteristicas());
        nuevoPabellon.setFechaCreacion(LocalDate.now());
        nuevoPabellon.setFechaActualizacion(LocalDate.now());

        nuevoPabellon.setSede(sede);
        nuevoPabellon.setTipo(tipo);
        nuevoPabellon.setEstado(estado);

        Pabellon pabellonGuardado = pabellonRepository.save(nuevoPabellon);

        return convertirARespuestaDTO(pabellonGuardado);
    }

    @Transactional(readOnly = true)
    public PabellonRespuestaDTO buscarPabellonPorId(Integer id) {
        Pabellon pabellon = pabellonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pabellón no encontrado con ID: " + id));
        return convertirARespuestaDTO(pabellon);
    }

    @Transactional(readOnly = true)
    public List<PabellonRespuestaDTO> listarPabellones() {
        return pabellonRepository.findAll().stream()
                .map(this::convertirARespuestaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void agregarImagenAPabellon(Integer pabellonId, MultipartFile archivoImagen, boolean esPrincipal) {
        if (esPrincipal) {
            pabellonImagenRepository.findByPabellonIdAndEsPrincipal(pabellonId, 1)
                    .ifPresent(imagenAnterior -> {
                        imagenAnterior.setEsPrincipal(0);
                        pabellonImagenRepository.save(imagenAnterior);
                    });
        }

        String nombreArchivoUnico = storageService.store(archivoImagen);

        Pabellon pabellon = pabellonRepository.findById(pabellonId)
                .orElseThrow(() -> new EntityNotFoundException("Pabellón no encontrado con ID: " + pabellonId));

        PabellonImagen nuevaImagen = new PabellonImagen();
        nuevaImagen.setPabellon(pabellon);
        nuevaImagen.setRutaArchivo(nombreArchivoUnico);
        nuevaImagen.setNombreArchivo(archivoImagen.getOriginalFilename());
        nuevaImagen.setTipoMime(archivoImagen.getContentType());
        nuevaImagen.setTamanoBytes((int) archivoImagen.getSize());
        nuevaImagen.setFechaSubida(LocalDate.now());
        nuevaImagen.setEsPrincipal(esPrincipal ? 1 : 0);

        pabellonImagenRepository.save(nuevaImagen);
    }

    @Transactional(readOnly = true)
    public List<PabellonRespuestaDTO> buscarDisponibles(LocalDate fecha, Integer estadoId, Integer tipoId) {
        return pabellonRepository.findPabellonesDisponibles(fecha, estadoId, tipoId).stream()
                .map(this::convertirARespuestaDTO) // Reutilizamos el método que ya tienes
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PabellonRespuestaDTO> buscarPabellonesPorIds(List<Integer> ids) {
        return pabellonRepository.findAllById(ids).stream()
                .map(this::convertirARespuestaDTO)
                .collect(Collectors.toList());
    }

    private PabellonRespuestaDTO convertirARespuestaDTO(Pabellon pabellon) {
        List<PabellonImagenDTO> imagenesDTO = pabellon.getImagenes() != null ?
                pabellon.getImagenes().stream().map(img -> {
                    PabellonImagenDTO dto = new PabellonImagenDTO();
                    dto.setId(img.getId());
                    dto.setRutaArchivo(img.getRutaArchivo());
                    dto.setEsPrincipal(img.getEsPrincipal());
                    return dto;
                }).collect(Collectors.toList()) : Collections.emptyList();

        EstadoPabellonDTO estadoDTO = new EstadoPabellonDTO();
        estadoDTO.setId(pabellon.getEstado().getId());
        estadoDTO.setNombre(pabellon.getEstado().getNombre());

        TipoPabellonDTO tipoDTO = new TipoPabellonDTO();
        tipoDTO.setId(pabellon.getTipo().getId());
        tipoDTO.setNombre(pabellon.getTipo().getNombre());

        ProvinciaDTO provinciaDTO = new ProvinciaDTO();
        provinciaDTO.setIdProvincia(pabellon.getSede().getComuna().getProvincia().getIdProvincia());
        provinciaDTO.setNombre(pabellon.getSede().getComuna().getProvincia().getNombre());

        ComunaDTO comunaDTO = new ComunaDTO();
        comunaDTO.setIdComuna(pabellon.getSede().getComuna().getIdComuna());
        comunaDTO.setNombre(pabellon.getSede().getComuna().getNombre());
        comunaDTO.setProvincia(provinciaDTO);

        SedeDTO sedeDTO = new SedeDTO();
        sedeDTO.setId(pabellon.getSede().getId());
        sedeDTO.setNombre(pabellon.getSede().getNombre());
        sedeDTO.setComuna(comunaDTO);

        return PabellonRespuestaDTO.builder()
                .id(pabellon.getId())
                .nombre(pabellon.getNombre())
                .descripcion(pabellon.getDescripcion())
                .capacidad(pabellon.getCapacidad())
                .edificio(pabellon.getEdificio())
                .piso(pabellon.getPiso())
                .referencia(pabellon.getReferencia())
                .caracteristicas(pabellon.getCaracteristicas())
                .estado(estadoDTO)
                .tipoPabellon(tipoDTO)
                .sede(sedeDTO)
                .imagenes(imagenesDTO)
                .build();
    }

    public List<PabellonRespuestaDTO> obtenerPabellonesPorEstado(Integer estadoId) {
        return pabellonRepository.findByEstado_Id(estadoId).stream()
                .map(this::mapearAPabellonRespuestaDTO)
                .collect(Collectors.toList());
    }

    private PabellonRespuestaDTO mapearAPabellonRespuestaDTO(Pabellon pabellon) {
        // idéntico a convertirARespuestaDTO, puedes reutilizarlo o unificarlo si querés
        return convertirARespuestaDTO(pabellon);
    }

    public List<PabellonRespuestaDTO> obtenerPabellonesPorEstadoYTipo(Integer estadoId, Integer tipoId) {
        return pabellonRepository.findByEstado_IdAndTipo_Id(estadoId, tipoId).stream()
                .map(this::convertirARespuestaDTO)
                .collect(Collectors.toList());
    }
}