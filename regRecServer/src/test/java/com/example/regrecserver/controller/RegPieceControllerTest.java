package com.example.regrecserver.controller;

import com.baeldung.openapi.model.AddRegPieceRequestDto;
import com.baeldung.openapi.model.RecommendationsResponseDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.baeldung.openapi.model.UpdateRegWeightingRequestDto;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.exceptions.RegPieceNotfound;
import com.example.regrecserver.exceptions.RegPieceWithContentExists;
import com.example.regrecserver.service.RegPieceService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class RegPieceControllerTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    RegPieceController regPieceController;

    @MockBean
    RegPieceService regPieceService;

    @DisplayName("Get all reg pieces")
    @Test
    void getAllRegPiecesHappy() throws Exception {
        List<RegPieceDto> regs = buildRegList();
        when(regPieceService.getAllRegPiece()).thenReturn(regs);
        String url = "/RegPiece";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(regs)));
    }

    @DisplayName("Add new reg piece happy")
    @Test
    void testAddRegHappy() throws Exception {
        AddRegPieceRequestDto requestDto = new AddRegPieceRequestDto();
        requestDto.setContent("ABC");
        requestDto.setParentNodeId(1);
        when(regPieceService.addNewRegPiece(requestDto)).thenReturn(buildReg());
        String url = "/RegPiece";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(buildReg())));
    }

    @DisplayName("Add reg missing params")
    @Test
    void testAddRegMissingParams() throws Exception {
        String url = "/RegPiece";
        AddRegPieceRequestDto requestDto = new AddRegPieceRequestDto();
        requestDto.setContent("ABC");
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("BAD_REQUEST")));

        requestDto = new AddRegPieceRequestDto();
        requestDto.setParentNodeId(1);
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("BAD_REQUEST")));
    }

    @DisplayName(("Add reg node not found"))
    @Test
    void testAddRegWhenNodeNotFound() throws Exception {
        AddRegPieceRequestDto requestDto = new AddRegPieceRequestDto();
        requestDto.setContent("ABC");
        requestDto.setParentNodeId(1);
        when(regPieceService.addNewRegPiece(requestDto)).thenThrow(new ElementNodeNotFound(String.format("Node ID %d not found", 1)));
        String url = "/RegPiece";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NODE_NOT_FOUND")));
    }

    @DisplayName("Add reg but content already exists")
    @Test
    void testAddRegContentExists() throws Exception {
        AddRegPieceRequestDto requestDto = new AddRegPieceRequestDto();
        requestDto.setContent("ABC");
        requestDto.setParentNodeId(1);
        when(regPieceService.addNewRegPiece(requestDto)).thenThrow(new RegPieceWithContentExists(String.format("Reg piece with content %s already exists", requestDto.getContent())));
        String url = "/RegPiece";
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("REG_PIECE_WITH_CONTENT_EXISTS")));
    }

    @DisplayName("Update Reg Piece Weighting Happy")
    @Test
    void testUpdateWeightHappy() throws Exception {
        UpdateRegWeightingRequestDto requestDto = new UpdateRegWeightingRequestDto();
        requestDto.setWeighting(BigDecimal.valueOf(50));
        when(regPieceService.updatePieceWeighting(1, requestDto.getWeighting().doubleValue())).thenReturn(buildReg());
        int nodeId = 1;
        String url = String.format("/RegPiece/%d/weighting", nodeId);
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(buildReg())));
    }

    @DisplayName("Update Reg Piece weighting missing params")
    @Test
    void testUpdateWeightingMissingParams() throws Exception {
        UpdateRegWeightingRequestDto requestDto = new UpdateRegWeightingRequestDto();
        int nodeId = 1;
        String url = String.format("/RegPiece/%d/weighting", nodeId);
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update Reg weighting reg piece not found")
    @Test
    void testAddRegWeightingWhenNodeNotFound() throws Exception {
        UpdateRegWeightingRequestDto requestDto = new UpdateRegWeightingRequestDto();
        requestDto.setWeighting(BigDecimal.valueOf(50));
        when(regPieceService.updatePieceWeighting(1, requestDto.getWeighting().doubleValue())).thenThrow(new RegPieceNotfound(String.format("Reg piece with id %d not found", 1)));
        int nodeId = 1;
        String url = String.format("/RegPiece/%d/weighting", nodeId);
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .content((mapper.writeValueAsString(requestDto)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("REG_PIECE_NOT_FOUND")));
    }

    private List<RegPieceDto> buildRegList(){
        List<RegPieceDto> regPieceDtos = new ArrayList<>();
        regPieceDtos.add(buildReg());
        return regPieceDtos;
    }

    private RegPieceDto buildReg()
    {
        RegPieceDto reg = new RegPieceDto();
        reg.setSampleSize(1);
        reg.setWeighting(BigDecimal.valueOf(0.5));
        reg.setContent("ABC");
        reg.setId(1);
        return reg;
    }
}
