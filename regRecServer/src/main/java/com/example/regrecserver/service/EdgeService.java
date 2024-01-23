package com.example.regrecserver.service;

import com.baeldung.openapi.model.EdgeRequestDto;
import com.baeldung.openapi.model.EdgeResponseDto;
import com.baeldung.openapi.model.ElementNodeResponseDto;
import com.example.regrecserver.entity.Edge;
import com.example.regrecserver.entity.ElementNode;
import com.example.regrecserver.exceptions.EdgeNotFound;
import com.example.regrecserver.exceptions.EdgePairingAlreadyExists;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.exceptions.MissingParamsError;
import com.example.regrecserver.repository.EdgeRepository;
import com.example.regrecserver.repository.ElementNodeRepository;
import com.example.regrecserver.utility.MapperService;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.*;

@Service
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final ElementNodeRepository elementNodeRepository;

    public EdgeService(EdgeRepository edgeRepository, ElementNodeRepository elementNodeRepository){
        this.edgeRepository = edgeRepository;
        this.elementNodeRepository = elementNodeRepository;
    }

    public List<EdgeResponseDto> getAllEdges(){
        List<EdgeResponseDto> edgeResponseDtos = new ArrayList<>();
        Iterable<Edge> edges = edgeRepository.findAll();
        edges.forEach((p) -> edgeResponseDtos.add(MapperService.INSTANCE.edgeToEdgeResponseDto(p)));
        return edgeResponseDtos;
    }

    public List<EdgeResponseDto> getEdgeWithNode(ElementNodeResponseDto nodeId){
        ElementNode node = MapperService.INSTANCE.elementNodeResponseDtoToElementNode(nodeId);
        List<Edge> edges = edgeRepository.findByNode1OrNode2(node, node);
        List<EdgeResponseDto> edgeResponseDtos = new ArrayList<>();
        edges.forEach((p) -> edgeResponseDtos.add(MapperService.INSTANCE.edgeToEdgeResponseDto(p)));
        return edgeResponseDtos;
    }

    public EdgeResponseDto findShortestEdgeFromList(List<EdgeResponseDto> edges)
    {
        EdgeResponseDto shortest = new EdgeResponseDto();
        shortest.setWeighting(BigDecimal.valueOf(999999999));
        for (EdgeResponseDto edge : edges) {
            if (edge.getWeighting().doubleValue() < shortest.getWeighting().doubleValue() && edge.getWeighting().doubleValue() > 0) // rejecting any weighting less than 0 as indicates no relation
            {
                shortest = edge;
            }
        }
        return shortest;
    }

    public List<EdgeResponseDto> sortListByWeighting(List<EdgeResponseDto> edges){
        edges.sort(Comparator.comparingDouble(edge -> edge.getWeighting().doubleValue()));
        return edges;
    }

    public EdgeResponseDto addNewEdge(EdgeRequestDto requestDto) throws ElementNodeNotFound, EdgePairingAlreadyExists{
        Edge newEdge = new Edge();
        //get nodes from ids and check if edge with node pair exists
        ElementNode node1 = elementNodeRepository.findById(requestDto.getNode1()).orElseThrow(()->new ElementNodeNotFound(String.format("Node ID %d not found", requestDto.getNode1())));
        ElementNode node2 = elementNodeRepository.findById(requestDto.getNode2()).orElseThrow(()->new ElementNodeNotFound(String.format("Node ID %d not found", requestDto.getNode2())));

        Optional<Edge> possibleEdge1 = edgeRepository.findByNode1AndNode2(node1, node2);
        Optional<Edge> possibleEdge2 = edgeRepository.findByNode1AndNode2(node2, node1);

        if(possibleEdge1.isPresent() || possibleEdge2.isPresent())
        {
            throw new EdgePairingAlreadyExists(String.format("Edge already exists between node Ids %d and %d",requestDto.getNode1(), requestDto.getNode2()));
        }

        if (requestDto.getWeighting() == null || requestDto.getSampleSize() == null) {
            newEdge.setWeighting(0.5);
            newEdge.setSample_size(1);
        } else {
            newEdge.setWeighting(requestDto.getWeighting().doubleValue());
            newEdge.setSample_size(requestDto.getSampleSize());
        }

        newEdge.setNode1(node1);
        newEdge.setNode2(node2);
        return MapperService.INSTANCE.edgeToEdgeResponseDto(edgeRepository.save(newEdge));
    }

    public EdgeResponseDto updateEdgeWeighting(int edgeId, double weighting) throws EdgeNotFound
    {
        Edge edge = edgeRepository.findById(edgeId).orElseThrow(()-> new EdgeNotFound(String.format("Edge with ID %d not found", edgeId)));
        double currentWeight = edge.getWeighting();
        int currentSample = edge.getSample_size();
        //increase average -> multiple current weight by sample size, add weighting sample to that, increase sample size, then divide by new sample size
        double newWeighting = ((currentWeight * currentSample) + weighting)/(currentSample + 1);
        edge.setWeighting(newWeighting);
        edge.setSample_size(currentSample+1);
        return MapperService.INSTANCE.edgeToEdgeResponseDto(edgeRepository.save(edge));
    }

    public EdgeResponseDto getEdgeByPairing(int id1, int id2) throws ElementNodeNotFound, EdgeNotFound {
        ElementNode node1 = elementNodeRepository.findById(id1).orElseThrow(() -> new ElementNodeNotFound(String.format("Node ID %d not found", id1)));
        ElementNode node2 = elementNodeRepository.findById(id2).orElseThrow(() -> new ElementNodeNotFound(String.format("Node ID %d not found", id2)));

        Optional<Edge> edge1 = edgeRepository.findByNode1AndNode2(node1, node2);
        Optional<Edge> edge2 = edgeRepository.findByNode1AndNode2(node2, node1);
        Edge edge;
        if(edge1.isEmpty() && edge2.isEmpty()) {
            throw new EdgeNotFound("Edge with pairing does not exist");
        }
        else {
            edge = edge1.orElseGet(edge2::get);
        }
        return MapperService.INSTANCE.edgeToEdgeResponseDto(edge);
    }
}
