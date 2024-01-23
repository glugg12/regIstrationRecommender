package com.example.regrecserver.controller;

import com.baeldung.openapi.model.RecommendationsResponseDto;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.service.RecommendService;
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

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecommendControllerTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    RecommendController recommendController;

    @MockBean
    RecommendService recommendService;

    @DisplayName("Test v1 happy path")
    @Test
    void testV1RecommendHappy() throws Exception {
        List<RecommendationsResponseDto> recs = buildRecommendations();
        when(recommendService.getRecommended(1)).thenReturn(recs);
        int nodeId = 1;
        String url = "/recommend";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nodeId", String.valueOf(nodeId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(recs)));
    }

    @DisplayName("Test v1 when node not exists")
    @Test
    void testV1NodeNotExists() throws Exception {
        when(recommendService.getRecommended(1)).thenThrow(new ElementNodeNotFound(String.format("Node ID %d not found", 1)));
        int nodeId = 1;
        String url = "/recommend";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nodeId", String.valueOf(nodeId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NODE_NOT_FOUND")));
    }

    @DisplayName("Test v1 missing params")
    @Test
    void testV1MissingParams() throws Exception {
        String url = "/recommend";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Test v2 Happy")
    @Test
    void testV2Happy() throws Exception {
        List<RecommendationsResponseDto> recs = buildRecommendations();
        when(recommendService.getDijkstraRecommended(1, 2)).thenReturn(recs);
        int nodeId = 1;
        int destId = 2;
        String url = "/recommend/dijkstra";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nodeId", String.valueOf(nodeId))
                        .param("destId", String.valueOf(destId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(recs)));
    }

    @DisplayName("Test v2 when node not exists")
    @Test
    void testV2NodeNotExists() throws Exception {
        when(recommendService.getDijkstraRecommended(1, 2)).thenThrow(new ElementNodeNotFound(String.format("Node ID %d not found", 1)));
        int nodeId = 1;
        int destId = 2;
        String url = "/recommend/dijkstra";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nodeId", String.valueOf(nodeId))
                        .param("destId", String.valueOf(destId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NODE_NOT_FOUND")));
    }

    @DisplayName("Test v2 missing params")
    @Test
    void testV2MissingParams() throws Exception {
        String url = "/recommend/dijkstra";
        int nodeId = 1;
        int destId = 2;
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nodeId", String.valueOf(nodeId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("destId", String.valueOf(destId))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private List<RecommendationsResponseDto> buildRecommendations()
    {
        List<RecommendationsResponseDto> recs = new ArrayList<>();
        RecommendationsResponseDto rec = new RecommendationsResponseDto();
        rec.setContent("ABCDEF");
        recs.add(rec);
        return recs;
    }
}
