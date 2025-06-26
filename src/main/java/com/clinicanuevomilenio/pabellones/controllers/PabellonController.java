package com.clinicanuevomilenio.pabellones.controllers;

import com.clinicanuevomilenio.pabellones.dto.*;
import com.clinicanuevomilenio.pabellones.repository.EstadoPabellonRepository;
import com.clinicanuevomilenio.pabellones.repository.TipoPabellonRepository;
import com.clinicanuevomilenio.pabellones.services.PabellonService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pabellones")
public class PabellonController {

    @Autowired
    private PabellonService pabellonService;

    @Autowired
    private EstadoPabellonRepository estadoPabellonRepository;

    @Autowired
    private TipoPabellonRepository tipoPabellonRepository;

    @PostMapping
    public ResponseEntity<?> crearPabellon(@RequestBody PabellonCreacionDTO dto) {
        try {
            PabellonRespuestaDTO pabellonCreado = pabellonService.crearPabellon(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(pabellonCreado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // FIX: Añadido para capturar cualquier otro error y evitar "Missing return statement"
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ocurrió un error inesperado."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPabellonPorId(@PathVariable Integer id) {
        try {
            PabellonRespuestaDTO pabellon = pabellonService.buscarPabellonPorId(id);
            return ResponseEntity.ok(pabellon);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // FIX: Añadido para capturar cualquier otro error y evitar "Missing return statement"
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Ocurrió un error inesperado."));
        }
    }

    @GetMapping
    public ResponseEntity<List<PabellonRespuestaDTO>> listarPabellones() {
        List<PabellonRespuestaDTO> pabellones = pabellonService.listarPabellones();
        return ResponseEntity.ok(pabellones);
    }

    @PostMapping("/{pabellonId}/imagenes")
    public ResponseEntity<?> subirImagen(
            @PathVariable Integer pabellonId,
            @RequestParam("file") MultipartFile archivo,
            @RequestParam(name = "esPrincipal", required = false, defaultValue = "false") boolean esPrincipal) {

        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Por favor, seleccione un archivo para subir."));
        }
        try {
            pabellonService.agregarImagenAPabellon(pabellonId, archivo, esPrincipal);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensaje", "Archivo subido y asociado con éxito."));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo subir el archivo: " + e.getMessage()));
        }
    }

    @GetMapping("/estado")
    public ResponseEntity<List<PabellonRespuestaDTO>> obtenerPabellonesPorEstado(
            @RequestParam Integer estadoId) {
        List<PabellonRespuestaDTO> filtrados = pabellonService.obtenerPabellonesPorEstado(estadoId);
        return ResponseEntity.ok(filtrados);
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<PabellonRespuestaDTO>> obtenerPorEstadoYTipo(
            @RequestParam Integer estadoId,
            @RequestParam Integer tipoId) {
        List<PabellonRespuestaDTO> resultados = pabellonService.obtenerPabellonesPorEstadoYTipo(estadoId, tipoId);
        return ResponseEntity.ok(resultados);
    }

    // --- NUEVOS ENDPOINTS PARA LOS FILTROS ---
    @GetMapping("/estados")
    public ResponseEntity<List<EstadoPabellonDTO>> listarTodosLosEstados() {
        List<EstadoPabellonDTO> estados = estadoPabellonRepository.findAll().stream()
                .map(estado -> {
                    EstadoPabellonDTO dto = new EstadoPabellonDTO();
                    dto.setId(estado.getId());
                    dto.setNombre(estado.getNombre());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(estados);
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<TipoPabellonDTO>> listarTodosLosTipos() {
        List<TipoPabellonDTO> tipos = tipoPabellonRepository.findAll().stream()
                .map(tipo -> {
                    TipoPabellonDTO dto = new TipoPabellonDTO();
                    dto.setId(tipo.getId());
                    dto.setNombre(tipo.getNombre());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<PabellonRespuestaDTO>> buscarPabellonesDisponibles(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer estadoId,
            @RequestParam(required = false) Integer tipoId) {

        List<PabellonRespuestaDTO> pabellones = pabellonService.buscarDisponibles(fecha, estadoId, tipoId);
        return ResponseEntity.ok(pabellones);
    }

    /**
     * Obtiene una lista de pabellones a partir de una lista de IDs.
     * Este endpoint es llamado por PabellonClientService en la otra API.
     * @param ids Lista de IDs de los pabellones a buscar.
     * @return Lista de DTOs de los pabellones encontrados.
     */
    @GetMapping("/por-ids")
    public ResponseEntity<List<PabellonRespuestaDTO>> obtenerPabellonesPorIds(@RequestParam String ids) {
        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        List<PabellonRespuestaDTO> pabellones = pabellonService.buscarPabellonesPorIds(idList);
        return ResponseEntity.ok(pabellones);
    }
} // <-- FIX: Asegúrate de que esta es la última llave de cierre de la clase}