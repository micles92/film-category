package org.film.service.validation;

import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

@Service
public class FilmValidationService implements ValidationService {
    private static final int MIN_PRODUCTION_YEAR = 1888;
    private static final int MAX_PRODUCTION_YEAR = 2100;
    private static final long MAX_FILE_SIZE_BYTES = (long) 1024 * 1024 * 1024;

    @Override
    public void validate(final String title,final String director,final int productionYear, final long fileSize) {
        validateTitle(title);
        validateDirector(director);
        validateProductionYear(productionYear);
        validateFileSize(fileSize);
    }

    private void validateTitle(String title) {
        if (StringUtils.isBlank(title)) {
            throw new IllegalArgumentException("Title cannot be null, empty, or blank");
        }
    }

    private void validateDirector(String director) {
        if (StringUtils.isBlank(director)) {
            throw new IllegalArgumentException("Director cannot be null, empty, or blank");
        }
    }

    private void validateProductionYear(int productionYear) {
        if (productionYear < MIN_PRODUCTION_YEAR || productionYear > MAX_PRODUCTION_YEAR) {
            throw new IllegalArgumentException(String.format("Production year must be between %d and %d", MIN_PRODUCTION_YEAR, MAX_PRODUCTION_YEAR));
        }
    }

    private void validateFileSize(long fileSize) {
        if (fileSize <= 0 || fileSize > MAX_FILE_SIZE_BYTES) {
            throw new IllegalArgumentException(String.format("File size must be greater than 0 and less than or equal to %d bytes", MAX_FILE_SIZE_BYTES));
        }
    }
}