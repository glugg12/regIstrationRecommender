package com.example.regrecserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class RegPiece {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer size;

    private String content;

    @JoinColumn
    @ManyToOne
    private ElementNode parent;

    private Double weighting;
    private Integer sampleSize;
}
