package com.example.regrecserver.service;

import com.baeldung.openapi.model.RegPieceDto;
import com.example.regrecserver.TestUtils;
import com.example.regrecserver.exceptions.ElementNodeNotFound;
import com.example.regrecserver.repository.EdgeRepository;
import com.example.regrecserver.repository.ElementNodeRepository;
import com.example.regrecserver.repository.RegPieceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RecommendServiceTest {
    @MockBean
    ElementNodeRepository elementNodeRepository;
    @MockBean
    RegPieceRepository regPieceRepository;
    @MockBean
    EdgeRepository edgeRepository;

    @MockBean
    ElementNodeService elementNodeService;

    @Autowired
    RecommendService recommendService;

    TestUtils testUtils = new TestUtils();

    @Test
    @DisplayName("Test v1 recommend")
    void testV1Recommend() throws ElementNodeNotFound {
        List<RegPieceDto> list1 = testUtils.buildRecommendedPiecesList("ABC", "DEF", "GHIJ");
        List<RegPieceDto> list2 = testUtils.buildRecommendedPiecesList("123","CAR","ZOOM");
        when(elementNodeService.getAllRelations(any(int.class))).thenReturn(testUtils.buildSortedSingleRelationListDto());
        when(elementNodeService.getNodeRegPieces(1)).thenReturn(list1);
        when(elementNodeService.getNodeRegPieces(2)).thenReturn(list2);

        assertEquals(testUtils.buildExpectedFromBuiltRecommendationLists(list1, list2), recommendService.getRecommended(1));
    }
}
