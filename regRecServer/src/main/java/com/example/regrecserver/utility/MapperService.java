package com.example.regrecserver.utility;

import com.baeldung.openapi.model.EdgeResponseDto;
import com.baeldung.openapi.model.ElementNodeResponseDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.example.regrecserver.entity.Edge;
import com.example.regrecserver.entity.ElementNode;
import com.example.regrecserver.entity.RegPiece;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapperService {

    MapperService INSTANCE = Mappers.getMapper(MapperService.class);

    ElementNodeResponseDto elementNodetoElementNodeResponseDto(ElementNode elementNode);
    ElementNode elementNodeResponseDtoToElementNode(ElementNodeResponseDto elementNodeResponseDto);

    @Mapping(source = "node1", target = "node1")
    @Mapping(source = "node2", target = "node2")
    @Mapping(source = "sample_size", target = "sampleSize")
    EdgeResponseDto edgeToEdgeResponseDto(Edge edge);

    RegPieceDto regPieceToRegPieceDto(RegPiece regPiece);

}
