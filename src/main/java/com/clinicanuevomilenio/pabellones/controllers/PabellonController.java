package com.clinicanuevomilenio.pabellones.controllers;

import com.clinicanuevomilenio.pabellones.dto.PabellonCreacionDTO;
import com.clinicanuevomilenio.pabellones.dto.PabellonRespuestaDTO;
import com.clinicanuevomilenio.pabellones.services.PabellonService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pabellones")
public class PabellonController {


    @Autowired
    private PabellonService pabellonService;

    /**
     * Endpoint para crear un nuevo pabellón.
     * Corresponde a la operación POST.
     * @param dto El cuerpo de la petición con los datos del pabellón a crear.
     * @return El pabellón recién creado o un mensaje de error.
     */
    @PostMapping
    public ResponseEntity<?> crearPabellon(@RequestBody PabellonCreacionDTO dto) {
        try {
            PabellonRespuestaDTO pabellonCreado = pabellonService.crearPabellon(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(pabellonCreado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener un pabellón por su ID.
     * @param id El ID del pabellón, extraído de la ruta de la URL.
     * @return El pabellón encontrado o un error 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPabellonPorId(@PathVariable Integer id) {
        try {
            PabellonRespuestaDTO pabellon = pabellonService.buscarPabellonPorId(id);
            return ResponseEntity.ok(pabellon);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para obtener la lista de todos los pabellones.
     * @return Una lista con los datos de todos los pabellones.
     */
    @GetMapping
    public ResponseEntity<List<PabellonRespuestaDTO>> listarPabellones() {
        List<PabellonRespuestaDTO> pabellones = pabellonService.listarPabellones();
        return ResponseEntity.ok(pabellones);
    }

    @PostMapping("/{pabellonId}/imagenes")
    public ResponseEntity<?> subirImagen(
            @PathVariable Integer pabellonId,
            @RequestParam("file") MultipartFile archivo,
            // @RequestParam. opcional, su valor por defecto es false
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
}