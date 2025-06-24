package com.clinicanuevomilenio.pabellones.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemStorageService {

    @Value("${file.upload-dir}") // Inyecta la ruta desde application.properties
    private String uploadDir;

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("No se puede guardar un archivo vacío.");
            }

            Path uploadPath = Paths.get(uploadDir);
            // crear directorio si no existe
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar un nombre de archivo único para evitar colisiones
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Guardar el archivo en el disco
            try (InputStream inputStream = file.getInputStream()) {
                Path filePath = uploadPath.resolve(uniqueFilename);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                return uniqueFilename; // Devuelve el nombre único del archivo guardado
            }
        } catch (IOException e) {
            throw new RuntimeException("Falló al guardar el archivo.", e);
        }
    }

    public Resource loadAsResource(String filename) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            Path file = uploadPath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("No se pudo leer el archivo: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("No se pudo leer el archivo: " + filename, e);
        }
    }
}