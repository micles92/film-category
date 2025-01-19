package org.film.service.file;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class LocalFileStorageServiceTest {

    private LocalFileStorageService localFileStorageService;
    private final String folderPath = "testFolder";
    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        localFileStorageService = new LocalFileStorageService(folderPath);
        mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "content".getBytes());
    }

    @Test
    void shouldStoreFileCorrectly() {
        // Given
        String title = "FilmTitle";
        String director = "DirectorName";
        int productionYear = 2023;

        // When
        Optional<String> result = localFileStorageService.storeFile(mockFile, title, director, productionYear);

        // Then
        assertThat(result).isPresent();
        String savedFilePath = result.get();
        assertThat(savedFilePath).contains("testFolder");
        assertThat(savedFilePath).endsWith("FilmTitle-DirectorName-2023.txt");

        File savedFile = new File(savedFilePath);
        assertThat(savedFile.exists()).isTrue();
    }

    @Test
    void shouldCreateDirectoryIfNotExists() {
        // Given
        String title = "AnotherFilm";
        String director = "AnotherDirector";
        int productionYear = 2022;

        // When
        Optional<String> result = localFileStorageService.storeFile(mockFile, title, director, productionYear);

        // Then
        assertThat(result).isPresent();
        File folder = new File(folderPath);
        assertThat(folder.exists()).isTrue();
    }

    @Test
    void shouldCreateUniqueFileName() {
        // Given
        String title = "FilmTitle";
        String director = "DirectorName";
        int productionYear = 2023;

        // When
        String fileName = localFileStorageService.buildFileName(title, director, productionYear);

        // Then
        assertThat(fileName).isEqualTo("FilmTitle-DirectorName-2023");
    }

    @Test
    void shouldExtractFileExtension() {
        // Given
        String filename = "testfile.mp4";
        MockMultipartFile fileWithExtension = new MockMultipartFile("file", filename, "video/mp4", "content".getBytes());

        // When
        String extension = localFileStorageService.getFileExtension(fileWithExtension);

        // Then
        assertThat(extension).isEqualTo(".mp4");
    }

    @AfterAll
    static void tearDown() throws IOException {
        Path folder = Path.of("testFolder");
        if (Files.exists(folder)) {
            Files.walk(folder)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
