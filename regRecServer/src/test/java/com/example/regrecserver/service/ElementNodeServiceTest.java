package com.example.regrecserver.service;

import com.baeldung.openapi.model.ElementNodeRelationDto;
import com.baeldung.openapi.model.ElementNodeResponseDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.example.regrecserver.TestUtils;
import com.example.regrecserver.entity.ElementNode;
import com.example.regrecserver.exceptions.ElementNodeNameAlreadyExists;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.repository.ElementNodeRepository;
import com.example.regrecserver.repository.RegPieceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ElementNodeServiceTest {
    @MockBean
    ElementNodeRepository elementNodeRepository;
    @MockBean
    EdgeService edgeService;
    @MockBean
    RegPieceRepository regPieceRepository;
    @Autowired
    ElementNodeService elementNodeService;

    TestUtils testUtils = new TestUtils();

    @Test
    @DisplayName("Test get all nodes")
    void testGetAllNodes(){
        when(elementNodeRepository.findAll()).thenReturn(testUtils.buildNodeList());
        assertEquals(testUtils.buildNodeListDto(), elementNodeService.getAllElementNodes());
    }

    @Test
    @DisplayName("Test get closest node")
    void testGetClosestNode() throws ElementNodeNotFound {
        when(elementNodeRepository.findById(any(int.class))).thenReturn(Optional.of(testUtils.buildNode()));
        when(edgeService.getEdgeWithNode(any(ElementNodeResponseDto.class))).thenReturn(testUtils.buildListDto());
        //don't care what edge service is up to here, just with what we're doing
        when(edgeService.findShortestEdgeFromList(any())).thenReturn(testUtils.buildEdgeDto());

        assertEquals(testUtils.buildEdgeDto().getNode2(), elementNodeService.getClosestNode(1));
    }

    @Test
    @DisplayName("Test get all relations")
    void testGetAllRelations() throws ElementNodeNotFound {
        when(elementNodeRepository.findById(any(int.class))).thenReturn(Optional.of(testUtils.buildNode()));
        when(edgeService.getEdgeWithNode(any(ElementNodeResponseDto.class))).thenReturn(testUtils.unsortedEdgeListDto());
        when(edgeService.sortListByWeighting(any())).thenReturn(testUtils.sortedEdgeListDto());
        List<ElementNodeRelationDto> expected = testUtils.buildSortedRelationListDto();
        List<ElementNodeRelationDto> output = elementNodeService.getAllRelations(1);
        for(int i = 0; i < 4; i++)
        {
            assertEquals(expected.get(i), output.get(i));
        }
    }

    @Test
    @DisplayName("Test add element node")
    void testAddElementNode() throws ElementNodeNameAlreadyExists {
        when(elementNodeRepository.findAllByNameIgnoreCase(any())).thenReturn(new ArrayList<>());
        when(elementNodeRepository.save(any(ElementNode.class))).thenReturn(testUtils.buildNode());

        assertEquals(testUtils.buildNodeDto(), elementNodeService.addElementNode("A"));
    }

    @Test
    @DisplayName("Test get node reg pieces")
    void testGetNodeRegPieces() throws ElementNodeNotFound {
        when(elementNodeRepository.findById(any())).thenReturn(Optional.of(testUtils.buildNode()));
        when(regPieceRepository.findAllByParent(any())).thenReturn(testUtils.buildRegPieceList());
        List<RegPieceDto> expected = testUtils.buildRegPieceListDto();
        List<RegPieceDto> output = elementNodeService.getNodeRegPieces(1);
        assertEquals(expected, output);
    }

    @Test
    @DisplayName("Test get node by id")
    void testGetNodeById() throws ElementNodeNotFound {
        when(elementNodeRepository.findById(any())).thenReturn(Optional.of(testUtils.buildNode()));
        assertEquals(testUtils.buildNodeDto(), elementNodeService.getNodeById(1));
    }
}
