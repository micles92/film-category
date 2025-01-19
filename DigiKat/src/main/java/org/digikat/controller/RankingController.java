package org.digikat.controller;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.digikat.controller.request.AddRankingRequest;
import org.digikat.controller.response.RankingResponse;
import org.digikat.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
public class RankingController {

    private final RankingService rankingService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add")
    public ResponseEntity<String> addRanking(@RequestBody AddRankingRequest request) {
        rankingService.addRanking(request);
        return ResponseEntity.ok("ok");
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Film ranking fetched successfully",
                    content = @Content(schema = @Schema(implementation = RankingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Film not found",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public RankingResponse getFilmRanking(
            @RequestParam("film") String film) {
        return rankingService.getRankingByTitle(film);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ranking updated successfully",
                    content = @Content(schema = @Schema(implementation = RankingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Film not found",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<RankingResponse> updateRanking(
            @RequestParam String film,
            @RequestParam int criticsRating) {

        RankingResponse updatedRanking = rankingService.updateRankingByTitle(film, criticsRating);
        return ResponseEntity.ok(updatedRanking);
    }
}
