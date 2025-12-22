package com.industrial.AODB.Entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airport_allocation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recId;

    @Column(nullable = false, length = 100)
    private String airline;

    @Column(nullable = false, length = 50)
    private String flightNumber;

    @Column(nullable = false)
    private String originDate;   // STD

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(nullable = false, length = 20)
    private String sortPosition;

    private String operationTime;
    private String created;
    private String lastEditTime;
    private String operatorName;
}

