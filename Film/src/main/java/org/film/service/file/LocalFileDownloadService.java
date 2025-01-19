package org.film.service.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Slf4j
@Service
public class LocalFileDownloadService implements FileDownloadService {

    @Override
    public Optional<Resource> loadFileAsResource(final String filePath) {
        log.info("Attempting to load file as resource. Path: {}", filePath);
        File file = new File(filePath);

        if (file.exists()) {
            log.info("File found. Returning resource for path: {}", filePath);
            return Optional.of(new FileSystemResource(file));
        } else {
            log.warn("File not found at path: {}", filePath);
            return Optional.empty();
        }
    }
}
