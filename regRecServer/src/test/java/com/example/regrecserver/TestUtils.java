package com.example.regrecserver;

import com.baeldung.openapi.model.*;
import com.example.regrecserver.entity.Edge;
import com.example.regrecserver.entity.ElementNode;
import com.example.regrecserver.entity.RegPiece;
import com.example.regrecserver.utility.MapperService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestUtils {
    public List<EdgeResponseDto> buildListDto(){
        List<EdgeResponseDto> responseDtos = new ArrayList<>();
        buildList().forEach((edge) -> {
            responseDtos.add(MapperService.INSTANCE.edgeToEdgeResponseDto(edge));
        });
        return responseDtos;
    }

    public List<Edge> buildList(){
        List<Edge> edges = new ArrayList<>();
        edges.add(buildEdge());
        return edges;
    }

    public List<RegPieceDto> buildRegList(){
        List<RegPieceDto> regPieceDtos = new ArrayList<>();
        regPieceDtos.add(buildReg());
        return regPieceDtos;
    }

    public ElementNodeResponseDto buildNodeDto()
    {
        ElementNodeResponseDto elementNodeResponseDto = new ElementNodeResponseDto();
        elementNodeResponseDto.setName("A");
        elementNodeResponseDto.setId(1);
        return elementNodeResponseDto;
    }

    public RegPieceDto buildReg()
    {
        RegPieceDto reg = new RegPieceDto();
        reg.setSampleSize(1);
        reg.setWeighting(BigDecimal.valueOf(0.5));
        reg.setContent("ABC");
        reg.setId(1);
        return reg;
    }
    public Edge buildEdge(){
        Edge edge = new Edge();
        edge.setId(1);
        edge.setNode1(buildNode());
        ElementNode node2 = buildNode();
        node2.setId(2);
        node2.setName("B");
        edge.setNode2(node2);
        edge.setWeighting(0.5);
        edge.setSample_size(1);
        return edge;
    }

    public ElementNode buildNode()
    {
        ElementNode node = new ElementNode();
        node.setName("A");
        node.setId(1);
        return node;
    }

    public EdgeResponseDto buildEdgeDto(){
        EdgeResponseDto edge = new EdgeResponseDto();
        edge.setWeighting(BigDecimal.valueOf(0.5));
        edge.setId(1);
        edge.setNode1(buildNodeDto());
        ElementNodeResponseDto n2= buildNodeDto();
        n2.setName("B");
        n2.setId(2);
        edge.setNode2(n2);
        edge.setSampleSize(1);
        return edge;
    }

    public List<ElementNode> buildNodeList()
    {
        List<ElementNode> nodes = new ArrayList<>();
        nodes.add(buildNode());
        return nodes;
    }

    public List<ElementNodeResponseDto> buildNodeListDto(){
        List<ElementNodeResponseDto> nodes = new ArrayList<>();
        nodes.add(buildNodeDto());
        return nodes;
    }

    public List<EdgeResponseDto> sortedEdgeListDto()
    {
        List<EdgeResponseDto> sort = buildListDto();
        sort.add(sort.get(0));
        sort.add(sort.get(0));
        sort.add(sort.get(0));

        sort.get(0).setWeighting(BigDecimal.valueOf(1.0));
        sort.get(1).setWeighting(BigDecimal.valueOf(2.0));
        sort.get(2).setWeighting(BigDecimal.valueOf(3.0));
        sort.get(3).setWeighting(BigDecimal.valueOf(4.0));
        return sort;
    }

    public List<EdgeResponseDto> unsortedEdgeListDto()
    {
        List<EdgeResponseDto> sort = buildListDto();
        sort.add(sort.get(0));
        sort.add(sort.get(0));
        sort.add(sort.get(0));

        sort.get(0).setWeighting(BigDecimal.valueOf(2.0));
        sort.get(1).setWeighting(BigDecimal.valueOf(4.0));
        sort.get(2).setWeighting(BigDecimal.valueOf(3.0));
        sort.get(3).setWeighting(BigDecimal.valueOf(1.0));
        return sort;
    }

    public List<ElementNodeRelationDto> buildSortedRelationListDto()
    {
        List<ElementNodeRelationDto> sorted = new ArrayList<>();
        List<EdgeResponseDto> edges = sortedEdgeListDto();
        edges.forEach((edge)->{
            ElementNodeRelationDto add = new ElementNodeRelationDto();
            add.setNode(edge.getNode2());
            add.setWeighting(edge.getWeighting());
            sorted.add(add);
        });
        return sorted;
    }

    public List<ElementNodeRelationDto> buildSortedSingleRelationListDto()
    {
        List<ElementNodeRelationDto> sorted = new ArrayList<>();
        List<EdgeResponseDto> edges = sortedEdgeListDto();
        edges.remove(3);
        edges.remove(2);
        edges.remove(1);
        edges.forEach((edge)->{
            ElementNodeRelationDto add = new ElementNodeRelationDto();
            add.setNode(edge.getNode2());
            add.setWeighting(edge.getWeighting());
            sorted.add(add);
        });
        return sorted;
    }

    public List<RegPiece> buildRegPieceList(){
        List<RegPiece> regs = new ArrayList<>();
        regs.add(buildRegPiece());
        return regs;
    }

    public RegPiece buildRegPiece(){
        RegPiece reg = new RegPiece();
        reg.setSampleSize(1);
        reg.setWeighting(0.5);
        reg.setParent(buildNode());
        reg.setSize(3);
        reg.setId(1);
        reg.setContent("ABC");
        return reg;
    }

    public List<RegPieceDto> buildRegPieceListDto(){
        List<RegPieceDto> regs = new ArrayList<>();
        regs.add(buildRegPieceDto());
        return regs;
    }

    public RegPieceDto buildRegPieceDto(){
        RegPieceDto reg = new RegPieceDto();
        reg.setSampleSize(1);
        reg.setWeighting(BigDecimal.valueOf(0.5));
        reg.setParent(buildNodeDto());
        reg.setSize(3);
        reg.setId(1);
        reg.setContent("ABC");
        return reg;
    }

    public List<RegPieceDto> buildRecommendedPiecesList(String content1, String content2, String content3)
    {
        List<RegPieceDto> list = new ArrayList<>();
        RegPieceDto reg = new RegPieceDto();
        RegPieceDto reg2 = new RegPieceDto();
        RegPieceDto reg3 = new RegPieceDto();

        reg.setId(1);
        reg2.setId(2);
        reg3.setId(3);

        reg.setContent(content1);
        reg2.setContent(content2);
        reg3.setContent(content3);

        reg.setSize(content1.length());
        reg2.setSize(content2.length());
        reg3.setSize(content3.length());

        reg.setWeighting(BigDecimal.valueOf(26));
        reg2.setWeighting(BigDecimal.valueOf(16));
        reg3.setWeighting(BigDecimal.valueOf(18));

        list.add(reg);
        list.add(reg2);
        list.add(reg3);
        return list;
    }

    public List<RecommendationsResponseDto> buildExpectedFromBuiltRecommendationLists(List<RegPieceDto> list1, List<RegPieceDto> list2)
    {
        List<RecommendationsResponseDto> expected = new ArrayList<>();
        list1.sort(Comparator.comparingDouble(reg -> reg.getWeighting().doubleValue()));
        Collections.reverse(list1);

        list2.sort(Comparator.comparingDouble(reg -> reg.getWeighting().doubleValue()));
        Collections.reverse(list2);
        list1.forEach((rec)->{
            RecommendationsResponseDto add = new RecommendationsResponseDto();
            add.setContent(rec.getContent());
            expected.add(add);
            list1.forEach((rec2)->{
                if(rec.getSize()+ rec2.getSize() <= 7)
                {
                    RecommendationsResponseDto  addInner = new RecommendationsResponseDto();
                    addInner.setContent(rec.getContent() + rec2.getContent());
                    expected.add(addInner);
                }
            });
        });

        list1.forEach((rec)->{
            list2.forEach((rec2)->{
                if(rec.getSize()+ rec2.getSize() <= 7)
                {
                    RecommendationsResponseDto  addInner = new RecommendationsResponseDto();
                    addInner.setContent(rec.getContent() + rec2.getContent());
                    expected.add(addInner);
                }
            });
        });

        return expected;

    }
}
