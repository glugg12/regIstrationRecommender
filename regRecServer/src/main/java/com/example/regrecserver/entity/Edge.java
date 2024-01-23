package com.example.regrecserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Edge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JoinColumn
    @ManyToOne
    private ElementNode node1;

    @JoinColumn
    @ManyToOne
    private ElementNode node2;

    @Column
    private double weighting;

    @Column
    private int sample_size;


}
