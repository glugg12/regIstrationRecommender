package com.example.regrecserver.controller;

import com.baeldung.openapi.api.RegPieceApi;
import com.baeldung.openapi.model.AddRegPieceRequestDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.baeldung.openapi.model.UpdateRegWeightingRequestDto;
import com.example.regrecserver.exceptions.*;
import com.example.regrecserver.service.RegPieceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegPieceController implements RegPieceApi {

    private final RegPieceService regPieceService;

    public RegPieceController(RegPieceService regPieceService){
        this.regPieceService = regPieceService;
    }

    public ResponseEntity<List<RegPieceDto>> getAllRegPieceGET(){
        return new ResponseEntity<>(regPieceService.getAllRegPiece(), HttpStatus.OK);
    }

    public ResponseEntity<RegPieceDto> addNewRegPiecePOST(AddRegPieceRequestDto requestDto){
        try{
            //check required are not null
            //can do this with tags and validators, need to come back to this.
            //this seems to only be a problem for object params
            if(requestDto.getContent() == null || requestDto.getParentNodeId() == null)
            {
                throw new MissingParamsError("Missing params in request!");
            }
            return new ResponseEntity<>(regPieceService.addNewRegPiece(requestDto), HttpStatus.OK);
        }
        catch(ElementNodeNotFound e)
        {
            throw new RegReccApplicationError(RegReccError.NODE_NOT_FOUND, e.getMessage());
        }
        catch(RegPieceWithContentExists e)
        {
            throw new RegReccApplicationError(RegReccError.REG_PIECE_WITH_CONTENT_EXISTS, e.getMessage());
        }
        catch(MissingParamsError e)
        {
            throw new RegReccApplicationError(RegReccError.BAD_REQUEST, e.getMessage());
        }
    }

    public ResponseEntity<RegPieceDto> updateRegWeightingPOST(Integer regId, UpdateRegWeightingRequestDto requestDto)
    {
        try{
            //check required are not null
            //can do this with tags and validators, need to come back to this.
            //this seems to only be a problem for object params
            if(requestDto.getWeighting() == null)
            {
                throw new MissingParamsError("Missing params in request!");
            }
            return new ResponseEntity<>(regPieceService.updatePieceWeighting(regId, requestDto.getWeighting().doubleValue()), HttpStatus.OK);
        }catch(RegPieceNotfound e)
        {
            throw new RegReccApplicationError(RegReccError.REG_PIECE_NOT_FOUND, e.getMessage());
        }
        catch(MissingParamsError e)
        {
            throw new RegReccApplicationError(RegReccError.BAD_REQUEST, e.getMessage());
        }
    }
}
