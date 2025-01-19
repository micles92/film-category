package org.film.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.film.model.FilmEntity;
import org.film.repository.FilmRepository;
import org.film.service.file.FileManagementService;
import org.film.utils.DigikatResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmControllerE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FileManagementService fileManagementService;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("digiKat.api-url", wireMockServer::baseUrl);
    }

    @BeforeEach
    void setUp() {
        filmRepository.deleteAll();
    }

    @Test
    void shouldAddFilmWithRankingEnrichmentFromDigikat() throws Exception {
        // Given
        String rankingJsonResponse = DigikatResponseUtils.createValidRankingJsonResponse("Shrek", 0, "DOBRY", "2025-01-19", 100);
        wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/ranking\\?film=[^&]+"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(rankingJsonResponse)));

        MockMultipartFile file = new MockMultipartFile("file", "testFilm.mp4", MediaType.MULTIPART_FORM_DATA_VALUE, "test content".getBytes());
        String title = "Shrek";
        String director = "Andrew Adamson";
        int productionYear = 2001;

        // When
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/films/addFilm")
                        .file(file)
                        .param("title", title)
                        .param("director", director)
                        .param("productionYear", String.valueOf(productionYear)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("Film added successfully with ID: ")));

        // Then
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();
        UUID responseFilmId = extractFilmIdFromResponse(responseContent);

        FilmEntity film = filmRepository.findById(responseFilmId).get();

        assertThat(film.getId()).isEqualTo(responseFilmId);
        assertThat(film.getTitle()).isEqualTo(title);
        assertThat(film.getDirector()).isEqualTo(director);
        assertThat(film.getProductionYear()).isEqualTo(productionYear);
        assertThat(film.getCriticsRating()).isEqualTo(100);
        assertTrue(fileManagementService.downloadFile(film.getPath(), film.getTitle()).exists());
    }

    @Test
    void shouldAddFilmWithZeroRankingWhenDigikatApiReturns404() throws Exception {
        // Given
        wireMockServer.stubFor(WireMock.get(WireMock.urlMatching("/ranking\\?film=[^&]+"))
                .willReturn(WireMock.aResponse()
                        .withStatus(404)
                ));

        MockMultipartFile file = new MockMultipartFile("file", "testFilm.mp4", MediaType.MULTIPART_FORM_DATA_VALUE, "test content".getBytes());
        String title = "Shrek2";
        String director = "Andrew Adamson";
        int productionYear = 2005;

        // When
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.multipart("/films/addFilm")
                        .file(file)
                        .param("title", title)
                        .param("director", director)
                        .param("productionYear", String.valueOf(productionYear)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("Film added successfully with ID: ")));

        // Then
        String responseContent = resultActions.andReturn().getResponse().getContentAsString();
        UUID responseFilmId = extractFilmIdFromResponse(responseContent);

        FilmEntity film = filmRepository.findById(responseFilmId).get();

        assertThat(film.getId()).isEqualTo(responseFilmId);
        assertThat(film.getTitle()).isEqualTo(title);
        assertThat(film.getDirector()).isEqualTo(director);
        assertThat(film.getProductionYear()).isEqualTo(productionYear);
        assertThat(film.getCriticsRating()).isEqualTo(0);
        assertTrue(fileManagementService.downloadFile(film.getPath(), film.getTitle()).exists());
    }

    @Test
    void shouldDownloadFilmSuccessfully() throws Exception {
        String title = "Terminator";
        String director = "James Cameron";
        String productionYear = "1984";

        mockMvc.perform(MockMvcRequestBuilders.multipart("/films/addFilm")
                        .file(new MockMultipartFile("file", "testFilm.mp4", MediaType.MULTIPART_FORM_DATA_VALUE, "test content".getBytes()))
                        .param("title", title)
                        .param("director", director)
                        .param("productionYear", productionYear))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("Film added successfully with ID: ")));

        mockMvc.perform(get("/films/downloadFilm")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Terminator-James Cameron-1984.mp4"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

    @ParameterizedTest
    @MethodSource("sortingParamsProvider")
    void shouldReturnSortedFilms(String sortBy, String direction, String expectedFirstFilm, String expectedSecondFilm) throws Exception {
        final FilmEntity film1 = FilmEntity.builder()
                .title("Shrek")
                .director("Andrew Adamson")
                .productionYear(2001)
                .fileSize(1024)
                .ranking(100)
                .criticsRating(80)
                .path("path1")
                .build();

        final FilmEntity film2 = FilmEntity.builder()
                .title("Toy Story")
                .director("John Lasseter")
                .productionYear(1995)
                .fileSize(2048)
                .ranking(80)  // Ranking is now 80 (lower than Shrek's ranking)
                .criticsRating(70)
                .path("path2")
                .build();

        filmRepository.save(film1);
        filmRepository.save(film2);

        mockMvc.perform(get("/films")
                        .param("sortBy", sortBy)
                        .param("direction", direction))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(expectedFirstFilm))
                .andExpect(jsonPath("$[1].title").value(expectedSecondFilm));
    }

    private static Stream<Object[]> sortingParamsProvider() {
        return Stream.of(
                new Object[]{"title", "desc", "Toy Story", "Shrek"},
                new Object[]{"productionYear", "asc", "Toy Story", "Shrek"},
                new Object[]{"ranking", "desc", "Shrek", "Toy Story"}
        );
    }

    private UUID extractFilmIdFromResponse(String responseContent) {
        String[] responseParts = responseContent.split(": ");
        return UUID.fromString(responseParts[1].trim());
    }
}
