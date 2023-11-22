package com.gft.api.crawler.controller;

import com.gft.api.crawler.domain.dto.ApiResponseDto;
import com.gft.api.crawler.domain.dto.CrawlDataDto;
import com.gft.api.crawler.domain.dto.CrawlPayload;
import com.gft.api.crawler.service.CrawlerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * Controller for Crawler
 */
@AllArgsConstructor
@RestController
@Tag(name = "Crawl")
@RequestMapping("/api/v1/crawler")
public class CrawlerController {


    private final CrawlerService service;


    /**
     * gets the crawl process info
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(description = "Gets a process of crawling by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = CrawlDataDto.class)
                            )
                    }),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)

    })
    public ResponseEntity getById(@PathVariable("id") String id) {
        Optional<CrawlDataDto> data = service.getCrawlDataById(id);

        if(data.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(data.get());
    }

    /**
     * crawl an url
     * @param payload
     * @return
     */
    @PostMapping
    @Operation(description = "Crawl a specific domain with given term")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    }),
            @ApiResponse(responseCode = "400", description = "Invalid Request",
                    content = {
                            @Content(
                                    schema = @Schema(implementation = ApiResponse.class)
                            )
                    }),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)

    })
    public ResponseEntity crawl(@RequestBody @Valid CrawlPayload payload) {
        try {
            var response = service.crawl(payload);
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.badRequest().body(new ApiResponseDto(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }




}
