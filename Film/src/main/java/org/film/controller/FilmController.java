package org.film.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.film.dto.FilmDto;
import org.film.service.FilmManager;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmManager filmManager;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Film added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/addFilm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addFilm(
            @RequestPart("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("director") String director,
            @RequestParam("productionYear") int productionYear
    ) {
        String filmId = filmManager.addFilm(file, title, director, productionYear);
        return ResponseEntity.ok("Film added successfully with ID: " + filmId);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Film downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "Film not found",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/downloadFilm")
    public ResponseEntity<Resource> downloadFilm(@RequestParam("title") String title) {
        Resource fileResource = filmManager.downloadFilmByTitle(title);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileResource.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileResource);
    }

    @GetMapping()
    public List<FilmDto> getSortedFilms(
            @RequestParam(name = "sortBy", defaultValue = "ranking") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(sortDirection, sortBy);
        return filmManager.findAll(sort);
    }

}
