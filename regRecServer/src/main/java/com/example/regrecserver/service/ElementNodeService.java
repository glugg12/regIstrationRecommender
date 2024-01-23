package com.example.regrecserver.service;

import com.baeldung.openapi.model.EdgeResponseDto;
import com.baeldung.openapi.model.ElementNodeRelationDto;
import com.baeldung.openapi.model.ElementNodeResponseDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.example.regrecserver.entity.ElementNode;
import com.example.regrecserver.entity.RegPiece;
import com.example.regrecserver.exceptions.ElementNodeNameAlreadyExists;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.exceptions.MissingParamsError;
import com.example.regrecserver.repository.ElementNodeRepository;
import com.example.regrecserver.repository.RegPieceRepository;
import com.example.regrecserver.utility.MapperService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElementNodeService {

    private final ElementNodeRepository elementNodeRepository;
    private final EdgeService edgeService;

    private final RegPieceRepository regPieceRepository;

    public ElementNodeService(ElementNodeRepository elementNodeRepository, EdgeService edgeService, RegPieceRepository regPieceRepository){
        this.elementNodeRepository = elementNodeRepository;
        this.edgeService = edgeService;
        this.regPieceRepository = regPieceRepository;
    }

    public List<ElementNodeResponseDto> getAllElementNodes(){
        List<ElementNodeResponseDto> output = new ArrayList<>();
        Iterable<ElementNode> elementNodes = elementNodeRepository.findAll();
        elementNodes.forEach((p) -> output.add(MapperService.INSTANCE.elementNodetoElementNodeResponseDto(p)));
        return output;
    }

    public ElementNodeResponseDto getClosestNode(int nodeId) throws ElementNodeNotFound
    {
        ElementNode elementNode = elementNodeRepository.findById(nodeId).orElseThrow(()->new ElementNodeNotFound(String.format("Node ID %d not found", nodeId)));
        ElementNodeResponseDto node = MapperService.INSTANCE.elementNodetoElementNodeResponseDto(elementNode);
        //get edges with this node
        List<EdgeResponseDto> edges = edgeService.getEdgeWithNode(node);
        //find the lowest weighted edge
        EdgeResponseDto shortest = edgeService.findShortestEdgeFromList(edges);
        //return other node on the edge
        if(shortest.getNode1().getId() == nodeId)
        {
            return shortest.getNode2();
        }
        else
        {
            return shortest.getNode1();
        }
    }

    public List<ElementNodeRelationDto> getAllRelations(int nodeId) throws  ElementNodeNotFound
    {
        ElementNode elementNode = elementNodeRepository.findById(nodeId).orElseThrow(()->new ElementNodeNotFound(String.format("Node ID %d not found", nodeId)));
        ElementNodeResponseDto node = MapperService.INSTANCE.elementNodetoElementNodeResponseDto(elementNode);
        List<ElementNodeRelationDto> relatedNodes = new ArrayList<>();
        List<EdgeResponseDto> edges = edgeService.getEdgeWithNode(node);
        edges = edgeService.sortListByWeighting(edges);
        edges.forEach((edge) -> {
            ElementNodeRelationDto relation = new ElementNodeRelationDto();
            if(edge.getNode1().getId() == nodeId){
                relation.setNode(edge.getNode2());
            }
            else
            {
                relation.setNode(edge.getNode1());
            }
            relation.setWeighting(edge.getWeighting());
            relatedNodes.add(relation);
        });
        return relatedNodes;
    }

    public ElementNodeResponseDto addElementNode(String nodeName) throws ElementNodeNameAlreadyExists
    {
        ElementNode newNode = new ElementNode();
        //check node with same name does not exist
        List<ElementNode> nodesWithSameName = elementNodeRepository.findAllByNameIgnoreCase(nodeName);
        if(nodesWithSameName.isEmpty())
        {
            newNode.setName(nodeName.toUpperCase());
            return(MapperService.INSTANCE.elementNodetoElementNodeResponseDto(elementNodeRepository.save(newNode)));
        }
        else
        {
            throw new ElementNodeNameAlreadyExists(String.format("Element name %s already exists", nodeName.toUpperCase()));
        }
    }

    public List<RegPieceDto> getNodeRegPieces(int nodeId) throws ElementNodeNotFound{
        //check node exists
        ElementNode node = elementNodeRepository.findById(nodeId).orElseThrow(()-> new ElementNodeNotFound(String.format("Node ID %d not found", nodeId)));
        List<RegPiece> regPieces = regPieceRepository.findAllByParent(node);
        List<RegPieceDto> output = new ArrayList<>();
        regPieces.forEach((reg)-> output.add(MapperService.INSTANCE.regPieceToRegPieceDto(reg)));
        return output;
    }

    public ElementNodeResponseDto getNodeById(int nodeId) throws ElementNodeNotFound{
        ElementNode node = elementNodeRepository.findById(nodeId).orElseThrow(()-> new ElementNodeNotFound(String.format("Node ID %d not found", nodeId)));
        return MapperService.INSTANCE.elementNodetoElementNodeResponseDto(node);
    }
}
