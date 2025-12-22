package com.industrial.AODB.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_airline_allocation_dom")
@Getter
@Setter
public class AirlineAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recId;

    @Column(name = "airline_code")
    private String airlineCode;

    @Column(name = "airline_name")
    private String airlineName;

    @Column(name = "operation")
    private String operation; // ARR or DEP

    @Column(name = "origin_date")
    private LocalDate originDate;

    @Column(name = "sort_position")
    private Integer sortPosition;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "last_edit_time")
    private LocalDateTime lastEditTime;

    @Column(name = "deleted")
    private Boolean deleted = false;

    @Column(name = "message_time_stamp")
    private LocalDateTime messageTimeStamp;

    @Column(name = "operation_time")
    private LocalDateTime operationTime;

    // 🔥 NEW COLUMN (Enable/Disable)
    @Column(name = "is_enbled")
    private Boolean enableStatus ;  // default = enabled
}
