package com.example.regrecserver.service;

import com.baeldung.openapi.model.EdgeRequestDto;
import com.baeldung.openapi.model.EdgeResponseDto;
import com.baeldung.openapi.model.ElementNodeResponseDto;
import com.example.regrecserver.TestUtils;
import com.example.regrecserver.entity.Edge;
import com.example.regrecserver.entity.ElementNode;
import com.example.regrecserver.exceptions.EdgeNotFound;
import com.example.regrecserver.exceptions.EdgePairingAlreadyExists;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.repository.EdgeRepository;
import com.example.regrecserver.repository.ElementNodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EdgeServiceTest {

    @MockBean
    EdgeRepository edgeRepository;

    @MockBean
    ElementNodeRepository elementNodeRepository;

    @Autowired
    EdgeService edgeService;


    TestUtils testUtils = new TestUtils();

    @Test
    @DisplayName("Test get all edges")
    void testGetAllEdges()
    {
        when(edgeRepository.findAll()).thenReturn(testUtils.buildList());
        assertEquals(testUtils.buildListDto(), edgeService.getAllEdges());
    }

    @Test
    @DisplayName("Test get edge with node in it")
    void testGetEdgeWithNode(){
        ElementNodeResponseDto elementNodeResponseDto = new ElementNodeResponseDto();
        elementNodeResponseDto.setId(1);
        elementNodeResponseDto.setName("A");
        when(edgeRepository.findByNode1OrNode2(any(), any())).thenReturn(testUtils.buildList());
        assertEquals(testUtils.buildListDto(), edgeService.getEdgeWithNode(elementNodeResponseDto));
    }

    @Test
    @DisplayName("Test get edge by pairing no pairing for node")
    void testGetEdgePairing(){
        ElementNodeResponseDto elementNodeResponseDto = new ElementNodeResponseDto();
        elementNodeResponseDto.setId(1);
        elementNodeResponseDto.setName("A");
        when(edgeRepository.findByNode1OrNode2(any(), any())).thenReturn(new ArrayList<Edge>());
        assertTrue(edgeService.getEdgeWithNode(elementNodeResponseDto).isEmpty());
    }

    @Test
    @DisplayName("Test find shortest edge from list")
    void testGetShortestEdge(){
        List<EdgeResponseDto> edgeResponseDtos = testUtils.buildListDto();
        EdgeResponseDto responseDto = testUtils.buildEdgeDto();
        responseDto.setWeighting(BigDecimal.valueOf(99));
        edgeResponseDtos.add(responseDto);
        assertEquals(testUtils.buildEdgeDto(), edgeService.findShortestEdgeFromList(edgeResponseDtos));
    }

    @Test
    @DisplayName("Test sort by weighting")
    void testSortByWeight(){
        List<EdgeResponseDto> edgeResponseDtos = testUtils.buildListDto();
        EdgeResponseDto responseDto = testUtils.buildEdgeDto();
        responseDto.setWeighting(BigDecimal.valueOf(99));
        edgeResponseDtos.add(responseDto);
        edgeResponseDtos.add(responseDto);
        edgeResponseDtos.add(responseDto);
        edgeResponseDtos.get(0).setWeighting(BigDecimal.valueOf(98));
        edgeResponseDtos.get(1).setWeighting(BigDecimal.valueOf(16));
        edgeResponseDtos.get(2).setWeighting(BigDecimal.valueOf(99));
        edgeResponseDtos.get(3).setWeighting(BigDecimal.valueOf(1));

        List<EdgeResponseDto> sorted = edgeResponseDtos;
        edgeResponseDtos.get(0).setWeighting(BigDecimal.valueOf(1));
        edgeResponseDtos.get(1).setWeighting(BigDecimal.valueOf(16));
        edgeResponseDtos.get(2).setWeighting(BigDecimal.valueOf(98));
        edgeResponseDtos.get(3).setWeighting(BigDecimal.valueOf(99));

        List<EdgeResponseDto> output = edgeService.sortListByWeighting(edgeResponseDtos);
        for(int i = 0; i < 4;i ++)
        {
            assertEquals(sorted.get(i),output.get(i));
        }
    }

    @Test
    @DisplayName("Test add edge correct return")
    void testAddEdge() throws ElementNodeNotFound, EdgePairingAlreadyExists {
        when(elementNodeRepository.findById(1)).thenReturn(Optional.of(testUtils.buildNode()));
        when(edgeRepository.save(any(Edge.class))).thenReturn(testUtils.buildEdge());
        ElementNode n2 = new ElementNode();
        n2.setName("B");
        n2.setId(2);
        when(elementNodeRepository.findById(2)).thenReturn(Optional.of(n2));
        EdgeRequestDto requestDto = new EdgeRequestDto();
        requestDto.setNode1(1);
        requestDto.setNode2(2);
        assertEquals(testUtils.buildEdgeDto(), edgeService.addNewEdge(requestDto));
    }

    @Test
    @DisplayName("Test update weighting")
    void testUpdateWeighting() throws EdgeNotFound {
        EdgeResponseDto expected = testUtils.buildEdgeDto();
        expected.setWeighting(BigDecimal.valueOf(2.0));
        expected.setSampleSize(2);

        Edge edge = testUtils.buildEdge();
        edge.setWeighting(2);
        edge.setSample_size(2);
        when(edgeRepository.findById(any(int.class))).thenReturn(Optional.of(testUtils.buildEdge()));
        when(edgeRepository.save(any(Edge.class))).thenReturn(edge);

        EdgeResponseDto output = edgeService.updateEdgeWeighting(1, 3.5);
        assertEquals(expected.getWeighting(), output.getWeighting());
        assertEquals(expected.getSampleSize(), output.getSampleSize());
    }
}
