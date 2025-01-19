package org.film.service.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final String folderPath;

    @Override
    public Optional<String> storeFile(final MultipartFile file, final String title, final String director, final int productionYear) {
        return Optional.of(createFolderIfNeeded())
                .flatMap(folder -> saveFile(file, title, director, productionYear, folder));
    }

    File createFolderIfNeeded() {
        final Path path = Paths.get(folderPath);
        final File folder = path.toFile();

        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                log.error("Failed to create directory: {}", folderPath);
                throw new FileStorageException("Could not create directory at " + folderPath);
            }
            log.info("Directory created: {}", folderPath);
        }
        return folder;
    }

    private Optional<String> saveFile(MultipartFile file, String title, String director, int productionYear, File folder) {
        String fileName = buildFileName(title, director, productionYear);
        String fileExtension = getFileExtension(file);
        String finalFileName = fileExtension != null ? fileName + fileExtension : fileName;

        final File fileToSave = new File(folder, finalFileName);

        try {
            file.transferTo(fileToSave);
            String relativePath = Paths.get(folderPath, finalFileName).toString();
            log.info("File successfully saved: {}", relativePath);
            return Optional.of(relativePath);
        } catch (IOException e) {
            log.error("Failed to save file: {}", finalFileName, e);
            return Optional.empty();
        }
    }

    String buildFileName(final String title, final String director, final int productionYear) {
        return String.format("%s-%s-%d", title, director, productionYear);
    }

    String getFileExtension(final MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
                .map(name -> name.substring(name.lastIndexOf(".")).toLowerCase())
                .filter(ext -> ext.length() > 1)
                .orElse(null);
    }
}
