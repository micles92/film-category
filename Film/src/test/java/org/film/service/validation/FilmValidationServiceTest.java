package org.film.service.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidationServiceTest {

    private final FilmValidationService filmValidationService = new FilmValidationService();

    @Test
    void shouldPassValidationWithValidInputs() {
        assertDoesNotThrow(() ->
                filmValidationService.validate("Inception", "Christopher Nolan", 2010, 500 * 1024 * 1024)
        );
    }

    @Test
    void shouldThrowExceptionForEmptyTitle() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                filmValidationService.validate("", "Christopher Nolan", 2010, 500 * 1024 * 1024)
        );
        assertEquals("Title cannot be null, empty, or blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyDirector() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                filmValidationService.validate("Inception", "", 2010, 500 * 1024 * 1024)
        );
        assertEquals("Director cannot be null, empty, or blank", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidProductionYear() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                filmValidationService.validate("Inception", "Christopher Nolan", 1800, 500 * 1024 * 1024)
        );
        assertEquals("Production year must be between 1888 and 2100", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForZeroFileSize() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                filmValidationService.validate("Inception", "Christopher Nolan", 2010, 0)
        );
        assertEquals("File size must be greater than 0 and less than or equal to 1073741824 bytes", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForFileSizeExceedingLimit() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                filmValidationService.validate("Inception", "Christopher Nolan", 2010, 2L * 1024 * 1024 * 1024)
        );
        assertEquals("File size must be greater than 0 and less than or equal to 1073741824 bytes", exception.getMessage());
    }
}
