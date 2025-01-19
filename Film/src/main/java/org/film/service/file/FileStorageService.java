package org.film.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FileStorageService {
    Optional<String> storeFile(MultipartFile file, String title, String director, int productionYear);
}
