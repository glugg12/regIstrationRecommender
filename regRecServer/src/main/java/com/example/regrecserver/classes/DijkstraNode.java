package com.example.regrecserver.classes;

import com.baeldung.openapi.model.ElementNodeResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class DijkstraNode {
    ElementNodeResponseDto node;

    double distance;

    List<DijkstraNode> neighbours = new ArrayList<>();

    public void addNeighbourAndSort(DijkstraNode node){
        neighbours.add(node);
        neighbours.sort(Comparator.comparingDouble(DijkstraNode::getDistance));
    }

    public DijkstraNode getClosestNeighbour()
    {
        return neighbours.get(0);
    }
}
