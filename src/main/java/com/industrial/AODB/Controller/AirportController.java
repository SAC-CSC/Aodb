package com.industrial.AODB.Controller;



import com.industrial.AODB.Entity.Airport;
import com.industrial.AODB.service.AirportService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/airports")
public class AirportController {

    private final AirportService service;

    public AirportController(AirportService service) {
        this.service = service;
    }

    // ✅ CRUD
    @GetMapping
    public List<Airport> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Airport add(@RequestBody Airport airport, @RequestParam(defaultValue = "system") String operator) {
        return service.add(airport, operator);
    }

    @PutMapping("/{recId}")
    public Airport update(@PathVariable Long recId,
                          @RequestBody Airport airport,
                          @RequestParam(defaultValue = "system") String operator) {
        return service.update(recId, airport, operator);
    }

    @DeleteMapping("/{recId}")
    public ResponseEntity<String> delete(@PathVariable Long recId) {
        service.delete(recId);
        return ResponseEntity.ok("Record deleted successfully!");
    }

    // ✅ Export to Excel
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=Airports.xlsx");

        List<Airport> airports = service.getAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Airports");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Airline");
        header.createCell(1).setCellValue("Flight Number");
        header.createCell(2).setCellValue("STD");
        header.createCell(3).setCellValue("Destination");
        header.createCell(4).setCellValue("Sort Position");

        int rowCount = 1;
        for (Airport a : airports) {
            Row row = sheet.createRow(rowCount++);
            row.createCell(0).setCellValue(a.getAirline());
            row.createCell(1).setCellValue(a.getFlightNumber());
            row.createCell(2).setCellValue(a.getOriginDate());
            row.createCell(3).setCellValue(a.getDestination());
            row.createCell(4).setCellValue(a.getSortPosition());
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // ✅ Export to PDF
    @GetMapping("/export/pdf")
    public void exportPDF(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=Airports.pdf");

        List<Airport> airports = service.getAll();
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        PdfPTable table = new PdfPTable(5);
        table.addCell("Airline");
        table.addCell("Flight Number");
        table.addCell("STD");
        table.addCell("Destination");
        table.addCell("Sort Position");

        for (Airport a : airports) {
            table.addCell(a.getAirline());
            table.addCell(a.getFlightNumber());
            table.addCell(a.getOriginDate());
            table.addCell(a.getDestination());
            table.addCell(a.getSortPosition());
        }

        document.add(table);
        document.close();
    }

    // ✅ Export to Word (as simple DOC using HTML)
    @GetMapping("/export/word")
    public void exportWord(HttpServletResponse response) throws IOException {
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment;filename=Airports.doc");

        List<Airport> airports = service.getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        sb.append("<tr><th>Airline</th><th>Flight Number</th><th>STD</th><th>Destination</th><th>Sort Position</th></tr>");

        for (Airport a : airports) {
            sb.append("<tr>")
                    .append("<td>").append(a.getAirline()).append("</td>")
                    .append("<td>").append(a.getFlightNumber()).append("</td>")
                    .append("<td>").append(a.getOriginDate()).append("</td>")
                    .append("<td>").append(a.getDestination()).append("</td>")
                    .append("<td>").append(a.getSortPosition()).append("</td>")
                    .append("</tr>");
        }
        sb.append("</table>");

        response.getWriter().write(sb.toString());
    }
}
