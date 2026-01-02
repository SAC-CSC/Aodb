package com.industrial.AODB.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="tblSortPosition")
public class SortPosition {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ REQUIRED
    @Column(name = "rec_id")
    private long recId;

    private String sortPosition;

    private Integer sortValue;

    private String type;

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
    private boolean deleted = false;
}
