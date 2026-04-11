package com.voting.backend.controller;

import com.voting.backend.dto.response.ApiResponse;
import com.voting.backend.dto.response.ResultsResponse;
import com.voting.backend.service.ExportService;
import com.voting.backend.service.ResultsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/results")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ResultsController {

    private final ResultsService resultsService;
    private final ExportService exportService;

    @GetMapping("/{electionId}")
    public ResponseEntity<ApiResponse<ResultsResponse>> getResults(@PathVariable Long electionId) {
        ResultsResponse results = resultsService.getResults(electionId);
        return ResponseEntity.ok(ApiResponse.success("Results retrieved successfully", results));
    }

    @GetMapping("/{electionId}/export/pdf")
    public ResponseEntity<byte[]> exportToPdf(@PathVariable Long electionId) {
        byte[] pdfBytes = exportService.exportResultsToPdf(electionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"election_" + electionId + "_results.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/{electionId}/export/csv")
    public ResponseEntity<byte[]> exportToCsv(@PathVariable Long electionId) {
        byte[] csvBytes = exportService.exportResultsToCsv(electionId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"election_" + electionId + "_results.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}