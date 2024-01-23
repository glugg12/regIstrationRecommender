package com.example.regrecserver.service;

import com.baeldung.openapi.model.AddRegPieceRequestDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.example.regrecserver.entity.ElementNode;
import com.example.regrecserver.entity.RegPiece;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.exceptions.RegPieceNotfound;
import com.example.regrecserver.exceptions.RegPieceWithContentExists;
import com.example.regrecserver.repository.ElementNodeRepository;
import com.example.regrecserver.repository.RegPieceRepository;
import com.example.regrecserver.utility.MapperService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RegPieceService {

    private final RegPieceRepository regPieceRepository;
    private final ElementNodeRepository elementNodeRepository;

    public RegPieceService(RegPieceRepository regPieceRepository, ElementNodeRepository elementNodeRepository){
        this.regPieceRepository = regPieceRepository;
        this.elementNodeRepository = elementNodeRepository;
    }

    public List<RegPieceDto> getAllRegPiece(){
        List<RegPieceDto> regPieceDtos = new ArrayList<>();
        Iterable<RegPiece> regPieces = regPieceRepository.findAll();
        regPieces.forEach((p)-> regPieceDtos.add(MapperService.INSTANCE.regPieceToRegPieceDto(p)));
        return regPieceDtos;
    }

    @SuppressWarnings("ExtractMethodRecommender")
    public RegPieceDto addNewRegPiece(AddRegPieceRequestDto requestDto) throws RegPieceWithContentExists, ElementNodeNotFound{
        //check piece doesn't already exist
        Optional<RegPiece> existCheck = regPieceRepository.findRegPieceByContentIgnoreCase(requestDto.getContent());
        if(existCheck.isPresent())
        {
            throw new RegPieceWithContentExists(String.format("Reg piece with content %s already exists", requestDto.getContent().toUpperCase()));
        }
        ElementNode elementNode = elementNodeRepository.findById(requestDto.getParentNodeId()).orElseThrow(()-> new ElementNodeNotFound(String.format("Node ID %d not found", requestDto.getParentNodeId())));
        RegPiece regPiece = new RegPiece();
        regPiece.setContent(requestDto.getContent().toUpperCase());
        regPiece.setSize(requestDto.getContent().length());
        regPiece.setParent(elementNode);
        if(requestDto.getWeighting() == null || requestDto.getSampleSize() == null)
        {
            //default weighting values
            regPiece.setWeighting(0.5);
            regPiece.setSampleSize(1);
        }
        else
        {
            regPiece.setSampleSize(requestDto.getSampleSize());
            regPiece.setWeighting(requestDto.getWeighting().doubleValue());
        }
        return MapperService.INSTANCE.regPieceToRegPieceDto(regPieceRepository.save(regPiece));
    }

    public RegPieceDto updatePieceWeighting(int regId, double weighting) throws RegPieceNotfound
    {
        RegPiece reg = regPieceRepository.findById(regId).orElseThrow(()-> new RegPieceNotfound(String.format("Reg piece with id %d not found", regId)));
        double currentWeighting = reg.getWeighting();
        int currentSample = reg.getSampleSize();
        double newWeighting = ((currentWeighting * currentSample) + weighting)/(currentSample + 1);
        reg.setWeighting(newWeighting);
        reg.setSampleSize(currentSample + 1);
        return MapperService.INSTANCE.regPieceToRegPieceDto(regPieceRepository.save(reg));
    }
}
