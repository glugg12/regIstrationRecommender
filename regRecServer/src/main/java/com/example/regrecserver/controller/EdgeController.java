package com.example.regrecserver.controller;

import com.baeldung.openapi.api.EdgeApi;
import com.baeldung.openapi.model.EdgeRequestDto;
import com.baeldung.openapi.model.EdgeResponseDto;
import com.baeldung.openapi.model.EdgeWeightingSampleRequestDto;
import com.example.regrecserver.exceptions.*;
import com.example.regrecserver.service.EdgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
@RestController
public class EdgeController implements EdgeApi {

    private final EdgeService edgeService;

    public EdgeController(EdgeService edgeService){
        this.edgeService = edgeService;
    }

    public ResponseEntity<List<EdgeResponseDto>> getAllEdgeGET(){
        return new ResponseEntity<>(edgeService.getAllEdges(), HttpStatus.OK);
    }

    public ResponseEntity<EdgeResponseDto> addEdgePOST(@RequestBody @Valid EdgeRequestDto requestDto){
        try{
            //check required are not null
            //can do this with tags and validators, need to come back to this.
            //this seems to only be a problem for object params
            if(requestDto.getNode1() == null || requestDto.getNode2()==null)
            {
                throw new MissingParamsError("Missing params in request!");
            }
            return new ResponseEntity<>(edgeService.addNewEdge(requestDto), HttpStatus.OK);
        }catch(EdgePairingAlreadyExists e)
        {
            throw new RegReccApplicationError(RegReccError.EDGE_PAIRING_ALREADY_EXISTS, e.getMessage());
        }
        catch(ElementNodeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
        catch(MissingParamsError e)
        {
            throw new RegReccApplicationError(RegReccError.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<EdgeResponseDto> addEdgeWeightingSamplePOST(Integer edgeId, EdgeWeightingSampleRequestDto requestDto)
    {
        try{
            //check required are not null
            //can do this with tags and validators, need to come back to this.
            //this seems to only be a problem for object params
            if(requestDto.getWeighting()==null)
            {
                throw new MissingParamsError("Missing params in request!");
            }
            return new ResponseEntity<>(edgeService.updateEdgeWeighting(edgeId, requestDto.getWeighting().doubleValue()), HttpStatus.OK);
        }
        catch (EdgeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.EDGE_NOT_FOUND, e.getMessage());
        }
        catch(MissingParamsError e)
        {
            throw new RegReccApplicationError(RegReccError.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<EdgeResponseDto> getEdgeByPairingGET(Integer node1, Integer node2){
        try{
            return new ResponseEntity<>(edgeService.getEdgeByPairing(node1, node2), HttpStatus.OK);
        }catch (ElementNodeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
        catch (EdgeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.EDGE_NOT_FOUND, e.getMessage());
        }
    }
}
