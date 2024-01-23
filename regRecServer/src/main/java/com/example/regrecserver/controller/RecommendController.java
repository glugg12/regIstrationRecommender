package com.example.regrecserver.controller;

import com.baeldung.openapi.api.RecommendApi;
import com.baeldung.openapi.model.RecommendationsResponseDto;
import com.example.regrecserver.classes.DijkstraNode;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.exceptions.RegReccApplicationError;
import com.example.regrecserver.exceptions.RegReccError;
import com.example.regrecserver.service.RecommendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommendController implements RecommendApi {

    RecommendService recommendService;

    public RecommendController(RecommendService recommendService){
        this.recommendService = recommendService;
    }
    public ResponseEntity<List<RecommendationsResponseDto>> getRecommendedGET(Integer nodeId){
        try
        {
            return new ResponseEntity<>(recommendService.getRecommended(nodeId), HttpStatus.OK);
        }catch(ElementNodeNotFound e){
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
    }
    public ResponseEntity<List<RecommendationsResponseDto>> getDijkstraRecommendedGET(Integer nodeId, Integer destId) {
        try{
            return new ResponseEntity<>(recommendService.getDijkstraRecommended(nodeId, destId), HttpStatus.OK);
        }
        catch(ElementNodeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
    }


}
