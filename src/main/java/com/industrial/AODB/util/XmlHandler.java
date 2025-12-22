package com.industrial.AODB.util;

import com.industrial.AODB.Entity.Flight;
import com.industrial.AODB.Repository.FlightRepository;
import com.industrial.AODB.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.xml.sax.InputSource;

@Slf4j
@Component
@RequiredArgsConstructor
public class XmlHandler {

    private final FlightService flightService;
    private final FlightRepository flightRepository;

    public void handleIncomingTelegram(String incomingStr) {
        String[] arr = incomingStr.split("\\u0002"); // STX
        for (String xmlPart : arr) {
            String xmlString = xmlPart.replace("\r\n", "").replace("\u0003", ""); // Remove ETX

            if (XmlValidator.validate(xmlString)) {
                parseXml(xmlString);
            } else {
                log.warn("Invalid XML received: {}", xmlString);
            }
        }
    }

    private void parseXml(String xmlContent) {
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new StringReader(xmlContent)));
            doc.getDocumentElement().normalize();

            Flight flight = new Flight();
            Element root = doc.getDocumentElement(); // <FlightInfo>

            // ✅ Meta fields
            flight.setCreated(LocalDateTime.now());
            flight.setLastEditTime(LocalDateTime.now());
            flight.setOperatorName("AUTO");
            flight.setDeleted(false);

            // ✅ Message timestamp from root attribute
            if (root.hasAttribute("TimeStamp")) {
                flight.setMessageTimeStamp(parseDateTime(root.getAttribute("TimeStamp")));
            }

            // ✅ Loop over direct children of <FlightInfo>
            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    switch (node.getNodeName()) {
                        case "Airline" -> flight.setAirline(node.getTextContent().trim());
                        case "FlightCode" -> flight.setFlightNumber(node.getTextContent().trim());
                        case "Date" -> flight.setOriginDate(parseDate(node.getTextContent().trim()));
                        case "FlightDestination" -> flight.setArrivalAirport(node.getTextContent().trim());
                        case "SortPosition" -> flight.setSortPosition(node.getTextContent().trim());
                        // case "Action" -> handle insert/update/delete later if needed
                    }
                }
            }

            // ✅ Save to DB
            flightRepository.save(flight);
            log.info("✈️ Flight inserted/updated: {} on {}",
                    flight.getFlightNumber(), flight.getOriginDate());

        } catch (Exception e) {
            log.error("❌ Error parsing XML: {}", e.getMessage(), e);
        }
    }

    private LocalDate parseDate(String raw) {
        try {
            return LocalDate.parse(raw, DateTimeFormatter.ISO_DATE);
        } catch (Exception e) {
            log.warn("⚠️ Failed to parse date: {}", raw);
            return null;
        }
    }

    private LocalDateTime parseDateTime(String raw) {
        try {
            return LocalDateTime.parse(raw.replace("Z", ""), DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("⚠️ Failed to parse datetime: {}", raw);
            return null;
        }
    }
}
