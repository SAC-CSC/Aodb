// 1. Flight Entity
package com.industrial.AODB.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tblFlightAllocation_Dom")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recId;

    private String airline;
    private String flightNumber;

    private String arrivalAirport;
    private String sortPosition;


    private String operatorName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate originDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime operationTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastEditTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime messageTimeStamp;


    @Column(nullable = false)
    private boolean deleted = false;  // ✅ better: rename to 'deleted'
}
