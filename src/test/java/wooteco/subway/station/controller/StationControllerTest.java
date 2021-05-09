package wooteco.subway.station.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.station.dto.StationRequest;
import wooteco.subway.station.dto.StationResponse;
import wooteco.subway.station.service.StationService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Station controller 테스트")
@WebMvcTest(StationController.class)
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StationService stationService;


    @DisplayName("지하철 역 생성")
    @Test
    void createStation() throws Exception {
        // given
        String 강남역 = "강남역";
        String content = objectMapper.writeValueAsString(new StationRequest(강남역));
        given(stationService.save(any(StationRequest.class)))
                .willReturn(new StationResponse(1L, 강남역));

        // when
        ResultActions response = mockMvc.perform(post("/stations")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/stations/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(강남역))
                .andDo(print());
        verify(stationService).save(any(StationRequest.class));
    }

    @DisplayName("모든 지하철 역 조회")
    @Test
    void showStations() throws Exception {
        // given
        given(stationService.findAll())
                .willReturn(Arrays.asList(
                        new StationResponse(1L, "강남역"),
                        new StationResponse(2L, "왕십리역")
                ));

        // when
        ResultActions response = mockMvc.perform(get("/stations"));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("강남역"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("왕십리역"))
                .andDo(print());
        verify(stationService).findAll();
    }

    @DisplayName("지하철 역 삭제")
    @Test
    void deleteStation() throws Exception {
        // given
        String id = "1";

        // when
        ResultActions response = mockMvc.perform(delete("/stations/" + id));

        // then
        response.andExpect(status().isNoContent())
                .andDo(print());
        verify(stationService).delete(any(Long.class));
    }
}