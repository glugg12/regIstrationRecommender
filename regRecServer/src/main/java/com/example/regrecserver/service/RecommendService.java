package com.example.regrecserver.service;

import com.baeldung.openapi.model.*;
import com.example.regrecserver.classes.DijkstraNode;
import com.example.regrecserver.entity.RegPiece;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.repository.EdgeRepository;
import com.example.regrecserver.repository.ElementNodeRepository;
import com.example.regrecserver.repository.RegPieceRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecommendService {

    ElementNodeRepository elementNodeRepository;
    RegPieceRepository regPieceRepository;
    EdgeRepository edgeRepository;

    ElementNodeService elementNodeService;

    public RecommendService(ElementNodeRepository elementNodeRepository, RegPieceRepository regPieceRepository, EdgeRepository edgeRepository, ElementNodeService elementNodeService){
        this.elementNodeRepository = elementNodeRepository;
        this.regPieceRepository = regPieceRepository;
        this.edgeRepository = edgeRepository;
        this.elementNodeService = elementNodeService;
    }
    public List<RecommendationsResponseDto> getRecommended(int nodeId) throws ElementNodeNotFound
    {
        List<ElementNodeRelationDto> relations = elementNodeService.getAllRelations(nodeId);
        Collections.reverse(relations);
        List<RegPieceDto> pieces = elementNodeService.getNodeRegPieces(nodeId);
        pieces.sort(Comparator.comparingDouble(reg -> reg.getWeighting().doubleValue()));
        Collections.reverse(pieces);
        List<RecommendationsResponseDto> recommendationsResponseDtos = new ArrayList<>();
        pieces.forEach((reg)->{
            if(reg.getWeighting().doubleValue() > 0)
            {
                RecommendationsResponseDto recommendationsResponseDto = new RecommendationsResponseDto();
                recommendationsResponseDto.setContent(reg.getContent());
                if(!recommendationsResponseDtos.contains(recommendationsResponseDto))
                {
                    recommendationsResponseDtos.add(recommendationsResponseDto);
                }
                //want to reject negative weightings as they suggest no correlation
                buildRecommendation(pieces, recommendationsResponseDtos, reg);
            }
        });


        //want to do next node after all current recs on this node
        pieces.forEach((reg)->{
            if(reg.getWeighting().doubleValue()> 0)
            {
                relations.forEach((relation)->{
                    if(relation.getWeighting().doubleValue() > 0)
                    {
                        List<RegPieceDto> nextNodePieces = null;
                        try {
                            nextNodePieces = elementNodeService.getNodeRegPieces(relation.getNode().getId());
                        } catch (ElementNodeNotFound e) {
                            //this should never occur as we pull our records from db for this
                            throw new RuntimeException(String.format("FATAL: Element node %d should exist here, but does not.", relation.getNode().getId()));
                        }
                        buildRecommendation(nextNodePieces, recommendationsResponseDtos, reg);
                    }
                });
            }
        });
        return recommendationsResponseDtos;
    }

    public List<RecommendationsResponseDto> getDijkstraRecommended(int nodeId, int destId) throws ElementNodeNotFound
    {
        //Dijkstra alg doesn't like negative numbers. It also looks for shortest path, whereas data is stored with heavy weighting. Need to do some magic.
        //what if, since numbers shouldn't get more than 50 due to the way the data is gathered, we find the diff between the node weight and 50
        //so with node 50 weight, 50-50 = 0, lower weight 49 becomes 50-49 = 1. Should do the trick.
        //unsure if having potential 0s in the graph weights messes with the alg, so a final shift of +1
        //Negatives become super high values in relation

        List<DijkstraNode> dijkstraNodes = getDijkstraNodes(nodeId);

        List<DijkstraNode> visited = new ArrayList<>();

        while(!dijkstraNodes.isEmpty())
        {
            DijkstraNode nextNode = getShortestDistanceNode(dijkstraNodes);
            List<ElementNodeRelationDto> adjacentNodes = elementNodeService.getAllRelations(nextNode.getNode().getId());
            adjacentNodes.forEach((adjacent) -> {
                if(adjacent.getWeighting().doubleValue() > 0){
                    int index = getListIndexByNode(adjacent.getNode(), dijkstraNodes); // if this returns -1 node is removed from list and has been visited already
                    if(index != -1)
                    {
                        if(nextNode.getDistance() + (50 - adjacent.getWeighting().doubleValue()) < dijkstraNodes.get(index).getDistance()){
                            dijkstraNodes.get(index).setDistance(nextNode.getDistance() + (50 - adjacent.getWeighting().doubleValue()));
                            nextNode.addNeighbourAndSort(dijkstraNodes.get(index));
                        }
                    }
                }
            });
            visited.add(nextNode);
        }

        List<DijkstraNode> shortestPath = new ArrayList<>();
        List<DijkstraNode> rejected = new ArrayList<>();
        shortestPath.add(visited.get(getListIndexByNode(elementNodeService.getNodeById(nodeId), visited)));
        boolean destReached = false;
        //dfs through resulting tree to find the shortest path
        while(!destReached)
        {
            DijkstraNode thisNode = shortestPath.get(shortestPath.size()-1);
            //add closest neighbour to list that isn't rejected path
            if(thisNode.getNeighbours().isEmpty())
            {
                rejected.add(thisNode);
                shortestPath.remove(shortestPath.size()-1);
            }
            else {
                boolean foundAcceptable = false;
                for(int i =0; i< thisNode.getNeighbours().size(); i++){
                    if(!rejected.contains(thisNode.getNeighbours().get(i)))
                    {
                        //add, don't care about the rest just yet
                        shortestPath.add(thisNode.getNeighbours().get(i));
                        foundAcceptable = true;
                        break;
                    }
                }
                if(!foundAcceptable){
                    //if we reach here, all childs are rejected
                    rejected.add(thisNode);
                    shortestPath.remove(shortestPath.size()-1);
                }
            }
            if(shortestPath.get(shortestPath.size()-1).getNode().getId() == destId)
            {
                destReached = true;
            }
        }

        List<RecommendationsResponseDto> recommendationsResponseDtos = new ArrayList<>();
        for(int i = 0; i < shortestPath.size(); i ++){
            //two trains of building reccs - we do front of path looking at dest, dest looking at source, then step along path each loop from each side
            ElementNodeResponseDto frontNode = shortestPath.get(0).getNode();
            List<RegPieceDto> frontPieces = elementNodeService.getNodeRegPieces(frontNode.getId());
            int index = i;
            frontPieces.forEach((piece) ->
            {
                List<RegPieceDto> targetNodePieces = null;
                try {
                    targetNodePieces = elementNodeService.getNodeRegPieces(shortestPath.get((shortestPath.size() - 1)-index).getNode().getId());
                } catch (ElementNodeNotFound e) {
                    //this should never occur as we pull our records from db for this
                    throw new RuntimeException(String.format("FATAL: Element node %d should exist here, but does not.", shortestPath.get(shortestPath.size()-index).getNode().getId()));
                }
                buildRecommendation(targetNodePieces, recommendationsResponseDtos, piece);
            });

            //doing opposite side of path
            ElementNodeResponseDto backNode = shortestPath.get(shortestPath.size()-1).getNode();
            List<RegPieceDto> backPieces = elementNodeService.getNodeRegPieces(backNode.getId());
            backPieces.forEach((piece) -> {
                List<RegPieceDto> targetNodePieces = null;
                try {
                    targetNodePieces = elementNodeService.getNodeRegPieces(shortestPath.get(index).getNode().getId());
                } catch (ElementNodeNotFound e) {
                    //this should never occur as we pull our records from db for this
                    throw new RuntimeException(String.format("FATAL: Element node %d should exist here, but does not.", shortestPath.get(index).getNode().getId()));
                }
                buildRecommendation(targetNodePieces, recommendationsResponseDtos, piece);
            });
        }
        return recommendationsResponseDtos;
    }

    private List<DijkstraNode> getDijkstraNodes(int nodeId) {
        List<ElementNodeResponseDto> nodes = elementNodeService.getAllElementNodes();
        List<DijkstraNode> dijkstraNodes = new ArrayList<>();
        nodes.forEach((node)->{
            DijkstraNode incomingNode = new DijkstraNode();
            incomingNode.setNode(node);
            incomingNode.setDistance(999999999); // let's use -1 to signify infinity
            if(node.getId() == nodeId)
            {
                incomingNode.setDistance(0);
            }
            dijkstraNodes.add(incomingNode);
        });
        return dijkstraNodes;
    }

    private void buildRecommendation(List<RegPieceDto> pieces, List<RecommendationsResponseDto> recommendationsResponseDtos, RegPieceDto reg) {
        pieces.forEach((reg2)->{
            if(reg2.getWeighting().doubleValue() > 0)
            {
                if((reg.getSize() + reg2.getSize()) <= 7)
                {
                    RecommendationsResponseDto recommendationsResponseDto = new RecommendationsResponseDto();
                    recommendationsResponseDto.setContent(String.format("%s%s", reg.getContent(), reg2.getContent()));
                    if(!recommendationsResponseDtos.contains(recommendationsResponseDto))
                    {
                        recommendationsResponseDtos.add(recommendationsResponseDto);
                    }
                }
            }
        });
    }

    private DijkstraNode getShortestDistanceNode(List<DijkstraNode> input)
    {
        DijkstraNode shortest = new DijkstraNode();
        shortest.setDistance(999999999);
        int index = 0;
        for(int i = 0; i < input.size(); i++){
            DijkstraNode compare = input.get(i);
            if(compare.getDistance() != -1 && compare.getDistance() < shortest.getDistance()){
                shortest = compare;
                index = i;
            }
        }
        input.remove(index);
        return shortest;
    }

    private int getListIndexByNode(ElementNodeResponseDto node, List<DijkstraNode> list){
        int index = -1;
        for(int i = 0; i < list.size(); i++){
            if(Objects.equals(list.get(i).getNode().getId(), node.getId())){
                index = i;
            }
        }
        return index;
    }
}
