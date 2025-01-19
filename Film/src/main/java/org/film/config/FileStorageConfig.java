package org.film.config;

import org.film.service.file.FileStorageService;
import org.film.service.file.LocalFileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class FileStorageConfig {

    @Value("${file.storage.folder}")
    private Path folderPath;

    @Bean
    public FileStorageService fileStorageService() {
        Path resolvedFolderPath = resolveFolderPath(folderPath);
        createDirectoryIfNotExists(resolvedFolderPath);
        return new LocalFileStorageService(resolvedFolderPath.toString());
    }

    private Path resolveFolderPath(Path folderPath) {
        return folderPath.isAbsolute() ? folderPath : Path.of(System.getProperty("user.dir")).resolve(folderPath);
    }

    private void createDirectoryIfNotExists(Path path) {
        try {
            Files.createDirectories(path);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create directory: " + path, e);
        }
    }
}
