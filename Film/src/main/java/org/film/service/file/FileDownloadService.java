package org.film.service.file;

import org.springframework.core.io.Resource;

import java.util.Optional;

public interface FileDownloadService {
    Optional<Resource> loadFileAsResource(String filePath);
}
