package com.example.regrecserver.controller;

import com.baeldung.openapi.model.ElementNodeRelationDto;
import com.baeldung.openapi.model.ElementNodeRequestDto;
import com.baeldung.openapi.model.ElementNodeResponseDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.example.regrecserver.exceptions.ElementNodeNameAlreadyExists;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.service.ElementNodeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ElementNodeControllerTest {
    @Autowired
    ElementNodeController elementNodeController;

    @MockBean
    ElementNodeService elementNodeService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @DisplayName("Get all nodes")
    @Test
    void getAllHappy() throws Exception {
        when(elementNodeService.getAllElementNodes()).thenReturn(buildElementList());
        String url = "/element-node";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"name\":\"A\"")));
    }

    @DisplayName("Get all nodes when no nodes")
    @Test
    void testWhenNoNodesGetAll() throws Exception {
        when(elementNodeService.getAllElementNodes()).thenReturn(new ArrayList<ElementNodeResponseDto>());
        String url = "/element-node";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @DisplayName("GetClosestNode Happy")
    @Test
    void testGetClosestHappy() throws Exception {
        ElementNodeResponseDto closest = buildNode();
        when(elementNodeService.getClosestNode(1)).thenReturn(closest);
        String url = String.format("/element-node/%d/closestNode", 1);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(mapper.writeValueAsString(closest))));
    }

    @DisplayName("Get closest node not found")
    @Test
    void testNodeNotFoundGetClosest() throws Exception {
        when(elementNodeService.getClosestNode(1)).thenThrow(new ElementNodeNotFound(String.format("Node ID %d not found", 1)));
        String url = String.format("/element-node/%d/closestNode", 1);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NODE_NOT_FOUND")));
    }

    @DisplayName("Get all relations happy")
    @Test
    void testGetAllRelations() throws Exception {
        List<ElementNodeRelationDto> relations = buildRelations();
        when(elementNodeService.getAllRelations(1)).thenReturn(relations);
        String url = String.format("/element-node/%d/nodeList", 1);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(mapper.writeValueAsString(relations))));
    }

    @DisplayName("Get all relations node not found")
    @Test
    void testRelationsNodeNotFound() throws Exception {
        when(elementNodeService.getAllRelations(1)).thenThrow(new ElementNodeNotFound(String.format("Node ID %d not found", 1)));
        String url = String.format("/element-node/%d/nodeList", 1);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NODE_NOT_FOUND")));
    }

    @DisplayName("Add node happy")
    @Test
    void testAddNodeHappy() throws Exception {
        ElementNodeResponseDto added = buildNode();
        ElementNodeRequestDto request = new ElementNodeRequestDto();
        request.setName(added.getName());
        when(elementNodeService.addElementNode(added.getName())).thenReturn(added);
        String url = "/element-node";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(request)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(mapper.writeValueAsString(added))));
    }

    @DisplayName("Add node missing params")
    @Test
    void testAddNodeMissingParams() throws Exception {
        ElementNodeRequestDto request = new ElementNodeRequestDto();

        String url = "/element-node";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(request)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("BAD_REQUEST")));
    }

    @DisplayName("Add node name already exists")
    @Test
    void testAddNodeNameExists() throws Exception {
        ElementNodeResponseDto added = buildNode();
        ElementNodeRequestDto request = new ElementNodeRequestDto();
        request.setName(added.getName());
        when(elementNodeService.addElementNode(added.getName())).thenThrow(new ElementNodeNameAlreadyExists(String.format("Element name %s already exists", added.getName())));
        String url = "/element-node";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(request)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString(added.getName())))
                .andExpect(content().string(containsString("NODE_NAME_ALREADY_EXISTS")));
    }

    @DisplayName("Get reg pieces happy")
    @Test
    void testGetRegPiecesHappy() throws Exception {
        RegPieceDto regPieceDto = buildRegPiece();
        List<RegPieceDto> regs = new ArrayList<>();
        regs.add(regPieceDto);
        when(elementNodeService.getNodeRegPieces(1)).thenReturn(regs);
        int nodeId = 1;
        String url = String.format("/element-node/%d/regPieces", nodeId);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(regs)));
    }

    @DisplayName("Get reg pieces when node not found")
    @Test
    void testGetRegPiecesNodeNotFound() throws Exception {
        when(elementNodeService.getNodeRegPieces(1)).thenThrow(new ElementNodeNotFound(String.format("Node ID %d not found", 1)));
        int nodeId = 1;
        String url = String.format("/element-node/%d/regPieces", nodeId);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NODE_NOT_FOUND")));
    }

    @DisplayName("Get node by ID happy")
    @Test
    void testGetNodeByIdHappy() throws Exception {
        ElementNodeResponseDto node = buildNode();
        when(elementNodeService.getNodeById(1)).thenReturn(node);
        int nodeId = 1;
        String url = String.format("/element-node/%d", nodeId);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(node)));
    }

    @DisplayName("Get node by Id node not found")
    @Test
    void testGetNodeByIdNotFound() throws Exception {
        when(elementNodeService.getNodeById(1)).thenThrow(new ElementNodeNotFound(String.format("Node ID %d not found", 1)));
        int nodeId = 1;
        String url = String.format("/element-node/%d", nodeId);
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NODE_NOT_FOUND")));
    }

    private List<ElementNodeResponseDto> buildElementList()
    {
        List<ElementNodeResponseDto> elementNodeResponseDtos = new ArrayList<>();
        ElementNodeResponseDto elementNodeResponseDto = new ElementNodeResponseDto();
        elementNodeResponseDto.setName("A");
        elementNodeResponseDto.setId(1);
        elementNodeResponseDtos.add(elementNodeResponseDto);
        return elementNodeResponseDtos;
    }

    private ElementNodeResponseDto buildNode()
    {
        ElementNodeResponseDto elementNodeResponseDto = new ElementNodeResponseDto();
        elementNodeResponseDto.setName("A");
        elementNodeResponseDto.setId(1);
        return elementNodeResponseDto;
    }

    private List<ElementNodeRelationDto> buildRelations()
    {
        List<ElementNodeRelationDto> relationDtos = new ArrayList<>();
        ElementNodeRelationDto elementNodeRelationDto = new ElementNodeRelationDto();
        elementNodeRelationDto.setNode(buildNode());
        elementNodeRelationDto.setWeighting(BigDecimal.valueOf(0.5));
        relationDtos.add(elementNodeRelationDto);
        return relationDtos;
    }

    private RegPieceDto buildRegPiece()
    {
        RegPieceDto regPieceDto = new RegPieceDto();
        regPieceDto.setId(1);
        regPieceDto.setContent("ABC");
        regPieceDto.setWeighting(BigDecimal.valueOf(0.5));
        regPieceDto.setSampleSize(1);
        return regPieceDto;
    }
}
