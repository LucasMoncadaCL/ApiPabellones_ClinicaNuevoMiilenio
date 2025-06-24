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

    @Autowired
    private PabellonRepository pabellonRepository;
    @Autowired
    private SedeRepository sedeRepository;
    @Autowired
    private TipoPabellonRepository tipoPabellonRepository;
    @Autowired
    private EstadoPabellonRepository estadoPabellonRepository;
    @Autowired
    private FileSystemStorageService storageService;
    @Autowired
    private PabellonImagenRepository pabellonImagenRepository;

    @Transactional
    public PabellonRespuestaDTO crearPabellon(PabellonCreacionDTO dto) {
        Sede sede = sedeRepository.findById(dto.getSedeId())
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada con ID: " + dto.getSedeId()));

        TipoPabellon tipoPabellon = tipoPabellonRepository.findById(dto.getTipoPabellonId())
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
        nuevoPabellon.setTipoPabellon(tipoPabellon);
        nuevoPabellon.setEstado(estado);

        Pabellon pabellonGuardado = pabellonRepository.save(nuevoPabellon);

        return convertirARespuestaDTO(pabellonGuardado);
    }

    /**
     * Busca un pabellón por su ID.
     * @param id El ID del pabellón a buscar.
     * @return El DTO del pabellón encontrado.
     */
    @Transactional(readOnly = true)
    public PabellonRespuestaDTO buscarPabellonPorId(Integer id) {
        Pabellon pabellon = pabellonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pabellón no encontrado con ID: " + id));
        return convertirARespuestaDTO(pabellon);
    }

    /**
     * Devuelve una lista de todos los pabellones.
     * @return Lista de DTOs de pabellones.
     */
    @Transactional(readOnly = true)
    public List<PabellonRespuestaDTO> listarPabellones() {
        return pabellonRepository.findAll()
                .stream()
                .map(this::convertirARespuestaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void agregarImagenAPabellon(Integer pabellonId, MultipartFile archivoImagen, boolean esPrincipal) {
        // si la nueva imagen se va a establecer como principal
        if (esPrincipal) {
            //si ya existe otra imagen principal para este pabellón
            pabellonImagenRepository.findByPabellonIdAndEsPrincipal(pabellonId, 1)
                    .ifPresent(imagenPrincipalAnterior -> {
                        // Si la encontramos, le quitamos el flag de principal
                        imagenPrincipalAnterior.setEsPrincipal(0);
                        pabellonImagenRepository.save(imagenPrincipalAnterior);
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

    /**
     * Convertir entidad Pabellon a su DTO de respuesta, manejando las conversiones de las entidades anidadas.
     * @param pabellon La entidad a convertir.
     * @return El DTO de respuesta completo y enriquecido.
     */
    private PabellonRespuestaDTO convertirARespuestaDTO(Pabellon pabellon) {
        List<PabellonImagenDTO> imagenesDTO = pabellon.getImagenes() != null ?
                pabellon.getImagenes().stream().map(img -> {
                    PabellonImagenDTO imgDto = new PabellonImagenDTO();
                    imgDto.setId(img.getId());
                    imgDto.setRutaArchivo(img.getRutaArchivo());
                    imgDto.setEsPrincipal(img.getEsPrincipal());
                    return imgDto;
                }).collect(Collectors.toList()) : Collections.emptyList();

        EstadoPabellonDTO estadoDTO = new EstadoPabellonDTO();
        estadoDTO.setId(pabellon.getEstado().getId());
        estadoDTO.setNombre(pabellon.getEstado().getNombre());

        TipoPabellonDTO tipoPabellonDTO = new TipoPabellonDTO();
        tipoPabellonDTO.setId(pabellon.getTipoPabellon().getId());
        tipoPabellonDTO.setNombre(pabellon.getTipoPabellon().getNombre());

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

        // Construcción del DTO de respuesta final usando el patrón Builder
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
                .tipoPabellon(tipoPabellonDTO)
                .sede(sedeDTO)
                .imagenes(imagenesDTO)
                .build();
    }
}
