package com.example.regrecserver.service;

import com.baeldung.openapi.model.AddRegPieceRequestDto;
import com.baeldung.openapi.model.RegPieceDto;
import com.example.regrecserver.TestUtils;
import com.example.regrecserver.entity.RegPiece;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.exceptions.RegPieceNotfound;
import com.example.regrecserver.exceptions.RegPieceWithContentExists;
import com.example.regrecserver.repository.ElementNodeRepository;
import com.example.regrecserver.repository.RegPieceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
public class RegPieceServiceTest {

    @MockBean
    RegPieceRepository regPieceRepository;
    @MockBean
    ElementNodeRepository elementNodeRepository;

    @Autowired
    RegPieceService regPieceService;

    TestUtils testUtils = new TestUtils();

    @Test
    @DisplayName("Test get all reg pieces")
    void testGetAllRegPieces(){
        when(regPieceRepository.findAll()).thenReturn(testUtils.buildRegPieceList());
        assertEquals(testUtils.buildRegPieceListDto(), regPieceService.getAllRegPiece());
    }

    @Test
    @DisplayName("Test add reg piece")
    void testAddRegPiece() throws ElementNodeNotFound, RegPieceWithContentExists {
        AddRegPieceRequestDto requestDto = new AddRegPieceRequestDto();
        requestDto.setContent("ABC");
        requestDto.setParentNodeId(1);
        when(regPieceRepository.save(any(RegPiece.class))).thenReturn(testUtils.buildRegPiece());
        when(elementNodeRepository.findById(1)).thenReturn(Optional.of(testUtils.buildNode()));
        assertEquals(testUtils.buildRegPieceDto(), regPieceService.addNewRegPiece(requestDto));
    }

    @Test
    @DisplayName("Test update reg piece weighting")
    void testUpdateWeighting() throws RegPieceNotfound {
        RegPiece updReg = testUtils.buildRegPiece();
        updReg.setWeighting(2.0);
        updReg.setSampleSize(2);
        when(regPieceRepository.findById(1)).thenReturn(Optional.of(testUtils.buildRegPiece()));
        when(regPieceRepository.save(any())).thenReturn(updReg);
        RegPieceDto result = regPieceService.updatePieceWeighting(1, 3.5);
        assertEquals(BigDecimal.valueOf(2.0), result.getWeighting());
        assertEquals(2, result.getSampleSize());
    }
}
