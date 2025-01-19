package org.film.service.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileDownloadServiceTest {

    private LocalFileDownloadService localFileDownloadService = new LocalFileDownloadService();;
    private Path tempFile;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("testFile", ".txt");
        Files.write(tempFile, "Test content".getBytes());
    }

    @Test
    void testLoadFileAsResource_FileExists() {
        String filePath = tempFile.toString();

        Optional<Resource> resource = localFileDownloadService.loadFileAsResource(filePath);

        assertTrue(resource.isPresent(), "Resource should be present when the file exists.");

        assertInstanceOf(FileSystemResource.class, resource.get(), "Resource should be of type FileSystemResource.");

        FileSystemResource fileSystemResource = (FileSystemResource) resource.get();
        assertEquals(filePath, fileSystemResource.getPath(), "File paths should match.");
    }

    @Test
    void testLoadFileAsResource_FileDoesNotExist() {
        String filePath = tempFile.toString() + "nonexistent";

        Optional<Resource> resource = localFileDownloadService.loadFileAsResource(filePath);

        assertFalse(resource.isPresent(), "Resource should not be present when the file does not exist.");
    }

    @Test
    void testLoadFileAsResource_FileIsEmpty() throws IOException {
        Path emptyFile = Files.createTempFile("emptyFile", ".txt");
        Files.write(emptyFile, "".getBytes());

        String filePath = emptyFile.toString();
        Optional<Resource> resource = localFileDownloadService.loadFileAsResource(filePath);

        assertTrue(resource.isPresent(), "Resource should be present even if the file is empty.");
        FileSystemResource fileSystemResource = (FileSystemResource) resource.get();
        assertEquals(filePath, fileSystemResource.getPath(), "File paths should match.");
    }

    @Test
    void testLoadFileAsResource_InvalidPath() {
        assertThrows(NullPointerException.class, () -> {
            localFileDownloadService.loadFileAsResource(null);
        });
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }
}
