package com.example.regrecserver.controller;

import com.baeldung.openapi.api.ElementNodeApi;
import com.baeldung.openapi.model.ElementNodeRelationDto;
import com.baeldung.openapi.model.ElementNodeRequestDto;
import com.baeldung.openapi.model.ElementNodeResponseDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.example.regrecserver.entity.RegPiece;
import com.example.regrecserver.exceptions.*;
import com.example.regrecserver.service.ElementNodeService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ElementNodeController implements ElementNodeApi {

    private final ElementNodeService elementNodeService;

    public ElementNodeController(ElementNodeService elementNodeService)
    {
        this.elementNodeService = elementNodeService;
    }

    public ResponseEntity<List<ElementNodeResponseDto>> getAllElementNodeGET()
    {
        return new ResponseEntity<>(elementNodeService.getAllElementNodes(), HttpStatus.OK);
    }

    public ResponseEntity<ElementNodeResponseDto> getClosestNodeGET(Integer nodeId)
    {
        try{
            return new ResponseEntity<>(elementNodeService.getClosestNode(nodeId), HttpStatus.OK);
        }
        catch(ElementNodeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<List<ElementNodeRelationDto>> getAttachedNodesGET(Integer nodeId){
        try{
            return new ResponseEntity<>(elementNodeService.getAllRelations(nodeId), HttpStatus.OK);
        }
        catch(ElementNodeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<ElementNodeResponseDto> addElementNodePOST(ElementNodeRequestDto requestDto)
    {
        try{
            //check required are not null
            //can do this with tags and validators, need to come back to this.
            //this seems to only be a problem for object params
            if(requestDto.getName() == null)
            {
                throw new MissingParamsError("Missing params in request!");
            }
            return new ResponseEntity<>(elementNodeService.addElementNode(requestDto.getName()), HttpStatus.OK);
        }
        catch(ElementNodeNameAlreadyExists e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NAME_ALREADY_EXISTS, e.getMessage());
        }
        catch(MissingParamsError e)
        {
            throw new RegReccApplicationError(RegReccError.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<List<RegPieceDto>> getNodeRegPiecesGET(Integer nodeId){
        try{
            return new ResponseEntity<>(elementNodeService.getNodeRegPieces(nodeId),HttpStatus.OK);
        }catch(ElementNodeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
    }

    public ResponseEntity<ElementNodeResponseDto> getNodeByIdGET(Integer nodeId)
    {
        try{
            return new ResponseEntity<>(elementNodeService.getNodeById(nodeId), HttpStatus.OK);
        }catch(ElementNodeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
    }
}
