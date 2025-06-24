package com.clinicanuevomilenio.pabellones.controllers;

import com.clinicanuevomilenio.pabellones.services.FileSystemStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/imagenes") // Una ruta base para todas las imágenes
public class FileController {

    @Autowired
    private FileSystemStorageService storageService;

    /**
     * Endpoint para obtener una imagen por su nombre de archivo.
     * @param filename El nombre único del archivo guardado en el disco.
     * @return El archivo de imagen como un recurso para ser mostrado en el navegador.
     */
    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            //archivo como recurso de nuestro almacenamiento
            Resource file = storageService.loadAsResource(filename);

            //intentar determinar el tipo de contenido del archivo para el navegador
            String contentType = Files.probeContentType(file.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            //respuesta HTTP con el archivo en el cuerpo y las cabeceras adecuadas
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .body(file);
        } catch (RuntimeException | IOException e) {
            // Si el archivo no se encuentra, 404
            return ResponseEntity.notFound().build();
        }
    }
}
