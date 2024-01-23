package com.example.regrecserver.controller;

import com.baeldung.openapi.model.EdgeRequestDto;
import com.baeldung.openapi.model.EdgeResponseDto;
import com.baeldung.openapi.model.EdgeWeightingSampleRequestDto;
import com.baeldung.openapi.model.ElementNodeResponseDto;
import com.example.regrecserver.exceptions.EdgeNotFound;
import com.example.regrecserver.exceptions.EdgePairingAlreadyExists;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.service.EdgeService;
import com.example.regrecserver.service.ElementNodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EdgeControllerTest {
    @MockBean
    private EdgeService edgeService;

    @Autowired
    private EdgeController edgeController;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Assert Happy Path")
    void testGetAllEdgeGET(){
        when(edgeService.getAllEdges()).thenReturn(buildEdgeList());
        ResponseEntity<List<EdgeResponseDto>> response = edgeController.getAllEdgeGET();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        EdgeResponseDto edge = response.getBody().get(0);
        assertEquals(buildEdge(), edge);
    }

    @Test
    @DisplayName("Assert when no edges, return empty list")
    void testGetAllEdgeGETWithEmptyList(){
        when(edgeService.getAllEdges()).thenReturn(new ArrayList<EdgeResponseDto>());
        ResponseEntity<List<EdgeResponseDto>> response = edgeController.getAllEdgeGET();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("Assert when POST add edge happy path")
    void testAddEdgePOST() throws ElementNodeNotFound, EdgePairingAlreadyExists {
        ElementNodeResponseDto node1 = buildElementNode(1, "Aaa");
        ElementNodeResponseDto node2 = buildElementNode(2, "Bbb");
        when(edgeService.addNewEdge(any())).thenReturn(buildEdgeWithCustomNodes(node1, node2));
        EdgeRequestDto requestDto = new EdgeRequestDto();
        requestDto.setNode1(node1.getId());
        requestDto.setNode2(node2.getId());
        ResponseEntity<EdgeResponseDto> response = edgeController.addEdgePOST(requestDto);
        assertEquals(HttpStatus.OK,response.getStatusCode());
        assertEquals(buildEdgeWithCustomNodes(node1, node2), response.getBody());
    }

    @DisplayName("Test when node not exists")
    @Test
    void testAddEdgeNodeNotFound() throws Exception {
        EdgeRequestDto requestDto = new EdgeRequestDto();
        requestDto.setNode1(1);
        requestDto.setNode2(2);
        when(edgeService.addNewEdge(requestDto)).thenThrow(new ElementNodeNotFound(String.format("Node ID %d not found", requestDto.getNode1())));


        mockMvc.perform(MockMvcRequestBuilders.post("/edge")
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NODE_NOT_FOUND")));
    }

    @DisplayName("Assert add edge with missing request body throws correct response")
    @ParameterizedTest
    @CsvSource(value = {"null,1","1,null","null,null"}, nullValues = "null")
    void testAddEdgeConstraints(Integer id1, Integer id2) throws Exception {
        EdgeRequestDto requestDto = new EdgeRequestDto();
        requestDto.setNode1(id1);
        requestDto.setNode2(id2);

        mockMvc.perform(MockMvcRequestBuilders.post("/edge")
                .content((mapper.writeValueAsString(requestDto)))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("BAD_REQUEST")));
    }

    @DisplayName("Add edge weighting sample happy")
    @Test
    void testAddEdgeWeighting() throws Exception {
        EdgeWeightingSampleRequestDto requestDto = new EdgeWeightingSampleRequestDto();
        requestDto.setWeighting(BigDecimal.valueOf(0.5));
        when(edgeService.updateEdgeWeighting(1, requestDto.getWeighting().doubleValue())).thenReturn(buildEdge());
        String url = String.format("/edge/%d/weighting", 1);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"weighting\":0.5")));
    }

    @DisplayName("Test addweighting null values")
    @Test
    void testAddWeightingNull() throws Exception{
        EdgeWeightingSampleRequestDto requestDto = new EdgeWeightingSampleRequestDto();
        String url = String.format("/edge/%d/weighting", 1);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("BAD_REQUEST")));
    }

    @DisplayName("Test addWeighting edge not found")
    @Test
    void testAddWeightEdgeNotFound() throws Exception{
        EdgeWeightingSampleRequestDto requestDto = new EdgeWeightingSampleRequestDto();
        requestDto.setWeighting(BigDecimal.valueOf(0.5));
        when(edgeService.updateEdgeWeighting(1, requestDto.getWeighting().doubleValue())).thenThrow(new EdgeNotFound(String.format("Edge with ID %d not found", 1)));
        String url = String.format("/edge/%d/weighting", 1);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("EDGE_NOT_FOUND")));
    }

    @DisplayName("Test getEdgeByPairing happy")
    @Test
    void testGetEdgePairingHappy() throws Exception {
        String url = "/edge/pairing";
        int node1 = 1;
        int node2 = 2;
        when(edgeService.getEdgeByPairing(node1, node2)).thenReturn(buildEdgeWithCustomNodes(buildElementNode(node1, "a"), buildElementNode(node2, "b")));
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("node1", Integer.toString(node1))
                        .param("node2", Integer.toString(node2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(buildEdgeWithCustomNodes(buildElementNode(node1, "a"), buildElementNode(node2, "b")))));
    }

    @DisplayName("Test edgePairing missing params")
    @Test
    void testGetEdgePairingMissingParams() throws Exception {
        String url = "/edge/pairing";
        int node1 = 1;
        int node2 = 2;
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("node1", Integer.toString(node1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("node2", Integer.toString(node2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Test edge pairing when no edge with pair")
    @Test
    void testEdgePairingUnhappy() throws Exception {
        String url = "/edge/pairing";
        int node1 = 1;
        int node2 = 2;
        when(edgeService.getEdgeByPairing(node1, node2)).thenThrow(new EdgeNotFound("Edge with pairing does not exist"));
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .param("node1", Integer.toString(node1))
                        .param("node2", Integer.toString(node2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("EDGE_NOT_FOUND")));
    }

    private List<EdgeResponseDto> buildEdgeList()
    {
        List<EdgeResponseDto> edgeResponseDtos = new ArrayList<>();
        edgeResponseDtos.add(buildEdge());
        return edgeResponseDtos;
    }

    private EdgeResponseDto buildEdge(){
        EdgeResponseDto edge = new EdgeResponseDto();
        edge.setWeighting(BigDecimal.valueOf(0.5));
        edge.setId(1);
        edge.setNode1(new ElementNodeResponseDto());
        edge.setNode2(new ElementNodeResponseDto());
        edge.setSampleSize(1);
        return edge;
    }

    private EdgeResponseDto buildEdgeWithCustomNodes(ElementNodeResponseDto node1, ElementNodeResponseDto node2){
        EdgeResponseDto edge = new EdgeResponseDto();
        edge.setWeighting(BigDecimal.valueOf(0.5));
        edge.setId(1);
        edge.setNode1(node1);
        edge.setNode2(node2);
        edge.setSampleSize(1);
        return edge;
    }

    private ElementNodeResponseDto buildElementNode(int Id, String name)
    {
        ElementNodeResponseDto node = new ElementNodeResponseDto();
        node.setId(Id);
        node.setName(name);
        return node;
    }
}
