package com.voting.backend.service;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;
import com.voting.backend.dto.response.ResultsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final ResultsService resultsService;

    public byte[] exportResultsToPdf(Long electionId) {
        ResultsResponse results = resultsService.getResults(electionId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Title
            Paragraph title = new Paragraph("ELECTION RESULTS REPORT")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY);
            document.add(title);

            // Election info
            document.add(new Paragraph(results.getElectionTitle())
                    .setFont(boldFont)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10));

            String generated = "Generated: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"));
            document.add(new Paragraph(generated)
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            // Summary table
            document.add(new Paragraph("\nSummary").setFont(boldFont).setFontSize(13));
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                    .useAllAvailableWidth();
            summaryTable.addCell(styledCell("Total Voter Turnout", boldFont, true));
            summaryTable.addCell(styledCell(String.valueOf(results.getTotalVotersTurnout()), regularFont, false));
            summaryTable.addCell(styledCell("Results Status", boldFont, true));
            summaryTable.addCell(styledCell(results.isResultsLocked() ? "Locked (Final)" : "Preliminary", regularFont, false));
            document.add(summaryTable);

            // Results per category
            for (ResultsResponse.CategoryResult categoryResult : results.getCategoryResults()) {
                document.add(new Paragraph("\nCategory: " + categoryResult.getCategoryName())
                        .setFont(boldFont)
                        .setFontSize(13)
                        .setMarginTop(20));

                document.add(new Paragraph("Total votes: " + categoryResult.getTotalVotesInCategory())
                        .setFont(regularFont)
                        .setFontSize(10));

                Table table = new Table(UnitValue.createPercentArray(new float[]{10, 35, 25, 15, 15}))
                        .useAllAvailableWidth();

                // Header row
                String[] headers = {"Rank", "Candidate", "Party", "Votes", "Percentage"};
                for (String h : headers) {
                    Cell headerCell = new Cell()
                            .add(new Paragraph(h).setFont(boldFont).setFontSize(10))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setPadding(6);
                    table.addHeaderCell(headerCell);
                }

                int rank = 1;
                for (ResultsResponse.CandidateResult cr : categoryResult.getCandidates()) {
                    boolean isWinner = cr.isWinner();
                    table.addCell(dataCell(String.valueOf(rank++), regularFont, isWinner));
                    String name = cr.getCandidateName() + (isWinner ? " 🏆" : "");
                    table.addCell(dataCell(name, boldFont, isWinner));
                    table.addCell(dataCell(cr.getParty() != null ? cr.getParty() : "-", regularFont, isWinner));
                    table.addCell(dataCell(String.valueOf(cr.getVoteCount()), regularFont, isWinner));
                    table.addCell(dataCell(cr.getPercentage() + "%", regularFont, isWinner));
                }
                document.add(table);
            }

            // Footer
            document.add(new Paragraph("\n\nThis document is system-generated and reflects the voting data at the time of export.")
                    .setFont(regularFont)
                    .setFontSize(9)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            document.close();
        } catch (IOException e) {
            log.error("Error generating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }

        return baos.toByteArray();
    }

    public byte[] exportResultsToCsv(Long electionId) {
        ResultsResponse results = resultsService.getResults(electionId);
        StringWriter sw = new StringWriter();

        try (CSVWriter csvWriter = new CSVWriter(sw)) {
            // Title row
            csvWriter.writeNext(new String[]{"Election Results Report"});
            csvWriter.writeNext(new String[]{"Election:", results.getElectionTitle()});
            csvWriter.writeNext(new String[]{"Total Voter Turnout:", String.valueOf(results.getTotalVotersTurnout())});
            csvWriter.writeNext(new String[]{"Results Status:", results.isResultsLocked() ? "Final (Locked)" : "Preliminary"});
            csvWriter.writeNext(new String[]{"Generated:", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))});
            csvWriter.writeNext(new String[]{});

            for (ResultsResponse.CategoryResult cat : results.getCategoryResults()) {
                csvWriter.writeNext(new String[]{"Category:", cat.getCategoryName()});
                csvWriter.writeNext(new String[]{"Total Votes in Category:", String.valueOf(cat.getTotalVotesInCategory())});
                csvWriter.writeNext(new String[]{"Rank", "Candidate Name", "Party", "Vote Count", "Percentage", "Winner"});

                int rank = 1;
                for (ResultsResponse.CandidateResult cr : cat.getCandidates()) {
                    csvWriter.writeNext(new String[]{
                            String.valueOf(rank++),
                            cr.getCandidateName(),
                            cr.getParty() != null ? cr.getParty() : "",
                            String.valueOf(cr.getVoteCount()),
                            cr.getPercentage() + "%",
                            cr.isWinner() ? "YES" : "NO"
                    });
                }
                csvWriter.writeNext(new String[]{});
            }
        } catch (IOException e) {
            log.error("Error generating CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate CSV report", e);
        }

        return sw.toString().getBytes();
    }

    private Cell styledCell(String content, PdfFont font, boolean isHeader) {
        Cell cell = new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(10))
                .setPadding(6);
        if (isHeader) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        }
        return cell;
    }

    private Cell dataCell(String content, PdfFont font, boolean highlight) {
        Cell cell = new Cell()
                .add(new Paragraph(content).setFont(font).setFontSize(10))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(5);
        if (highlight) {
            cell.setBackgroundColor(new com.itextpdf.kernel.colors.DeviceRgb(255, 255, 200));
        }
        return cell;
    }
}