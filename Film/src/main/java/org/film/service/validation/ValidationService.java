package org.film.service.validation;

public interface ValidationService {
    void validate(String title, String director, int productionYear, long fileSize);
}
