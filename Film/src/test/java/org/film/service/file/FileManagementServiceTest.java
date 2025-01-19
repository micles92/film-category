package org.film.service.file;

import org.film.service.FilmNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FileManagementServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FileDownloadService fileDownloadService;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private Resource resource;

    private FileManagementService fileManagementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileManagementService = new FileManagementService(fileStorageService, fileDownloadService);
    }

    @ParameterizedTest
    @CsvSource({
            "Inception, Christopher Nolan, 2010, /files/inception.mp4",
            "The Dark Knight, Christopher Nolan, 2008, /files/darkknight.mp4",
            "Interstellar, Christopher Nolan, 2014, /files/interstellar.mp4"
    })
    void storeFile_shouldStoreFileForVariousInputs(String title, String director, int productionYear, String storedFilePath) {
        // given
        when(fileStorageService.storeFile(multipartFile, title, director, productionYear))
                .thenReturn(Optional.of(storedFilePath));

        // when
        String result = fileManagementService.storeFile(multipartFile, title, director, productionYear);

        // then
        assertThat(result).isEqualTo(storedFilePath);
        verify(fileStorageService, times(1)).storeFile(multipartFile, title, director, productionYear);
    }

    @ParameterizedTest
    @CsvSource({
            "Inception, Christopher Nolan, 2010",
            "The Dark Knight, Christopher Nolan, 2008",
            "Interstellar, Christopher Nolan, 2014"
    })
    void storeFile_shouldThrowExceptionForMissingStorage(String title, String director, int productionYear) {
        // given
        when(fileStorageService.storeFile(multipartFile, title, director, productionYear))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> fileManagementService.storeFile(multipartFile, title, director, productionYear))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Error while storing file for film: " + title);

        verify(fileStorageService, times(1)).storeFile(multipartFile, title, director, productionYear);
    }

    @Test
    void storeFile_shouldHandleExceptionFromStorageService() {
        // given
        String title = "Inception";
        String director = "Christopher Nolan";
        int productionYear = 2010;

        when(fileStorageService.storeFile(multipartFile, title, director, productionYear))
                .thenThrow(new RuntimeException("Unexpected error"));

        // when & then
        assertThatThrownBy(() -> fileManagementService.storeFile(multipartFile, title, director, productionYear))
                .isInstanceOf(FileStorageException.class)
                .hasMessageContaining("Error while storing file for film: " + title)
                .hasCauseInstanceOf(RuntimeException.class);

        verify(fileStorageService, times(1)).storeFile(multipartFile, title, director, productionYear);
    }

    @ParameterizedTest
    @CsvSource({
            "/files/inception.mp4, Inception",
            "/files/darkknight.mp4, The Dark Knight",
            "/files/interstellar.mp4, Interstellar"
    })
    void downloadFile_shouldDownloadFileForVariousPaths(String path, String title) {
        // given
        when(fileDownloadService.loadFileAsResource(path)).thenReturn(Optional.of(resource));

        // when
        Resource result = fileManagementService.downloadFile(path, title);

        // then
        assertThat(result).isEqualTo(resource);
        verify(fileDownloadService, times(1)).loadFileAsResource(path);
    }

    @ParameterizedTest
    @CsvSource({
            "/files/nonexistent.mp4, Unknown Film",
            "/invalid/path.mp4, Invalid Film"
    })
    void downloadFile_shouldThrowExceptionForInvalidPaths(String path, String title) {
        // given
        when(fileDownloadService.loadFileAsResource(path)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> fileManagementService.downloadFile(path, title))
                .isInstanceOf(FilmNotFoundException.class)
                .hasMessageContaining("File not found for film: " + title);

        verify(fileDownloadService, times(1)).loadFileAsResource(path);
    }
}
