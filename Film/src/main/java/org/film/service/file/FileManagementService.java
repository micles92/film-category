package org.film.service.file;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.film.service.FilmNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManagementService {

    private final FileStorageService fileStorageService;
    private final FileDownloadService fileDownloadService;

    public String storeFile(MultipartFile file, String title, String director, int productionYear) {
        try {
            return fileStorageService.storeFile(file, title, director, productionYear)
                    .orElseThrow(() -> new FileStorageException("Failed to store file for film: " + title));
        } catch (Exception ex) {
            log.error("Error while storing file for film '{}': {}", title, ex.getMessage());
            throw new FileStorageException("Error while storing file for film: " + title, ex);
        }
    }

    public Resource downloadFile(String path, String title) {
        log.info("Attempting to load file for title '{}', Path: '{}'", title, path);

        return fileDownloadService.loadFileAsResource(path)
                .orElseThrow(() -> {
                    log.error("Failed to load file for film '{}'. Path: '{}'", title, path);
                    return new FilmNotFoundException("File not found for film: " + title);
                });
    }
}
