package wooteco.subway.line.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.line.dto.request.LineCreateRequest;
import wooteco.subway.line.dto.request.LineUpdateRequest;
import wooteco.subway.line.dto.response.LineCreateResponse;
import wooteco.subway.line.dto.response.LineResponse;
import wooteco.subway.line.service.LineService;
import wooteco.subway.section.dto.request.SectionCreateRequest;
import wooteco.subway.section.dto.response.SectionCreateResponse;
import wooteco.subway.section.service.SectionService;
import wooteco.subway.station.service.StationService;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Line controller 테스트")
@WebMvcTest(controllers = LineController.class)
class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LineService lineService;

    @MockBean
    private StationService stationService;

    @MockBean
    private SectionService sectionService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("지하철 노선 생성")
    @Test
    void createLine() throws Exception {
        // given
        String 분당선 = "분당선";
        String 빨간색 = "red";
        String content = objectMapper.writeValueAsString(new LineCreateRequest(분당선, 빨간색, 1L, 2L, 3));
        given(lineService.save(any(LineCreateRequest.class)))
                .willReturn(new LineCreateResponse(1L, 분당선, 빨간색));
        given(sectionService.save(any(LineCreateResponse.class), any(SectionCreateRequest.class)))
                .willReturn(new SectionCreateResponse(1L, 1L, 1L, 2L, 3));

        // when
        ResultActions response = mockMvc.perform(post("/lines")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/lines/1"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(분당선))
                .andExpect(jsonPath("$.color").value(빨간색))
                .andDo(print());
        verify(lineService).save(any(LineCreateRequest.class));
    }

    @DisplayName("모든 지하철 노선 생성")
    @Test
    void showLines() throws Exception {
        // given
        given(lineService.findAll())
                .willReturn(Arrays.asList(
                        new LineResponse(1L, "2호선", "green"),
                        new LineResponse(2L, "신분당선", "yellow")
                ));

        // when
        ResultActions response = mockMvc.perform(get("/lines"));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("2호선"))
                .andExpect(jsonPath("$[0].color").value("green"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("신분당선"))
                .andExpect(jsonPath("$[1].color").value("yellow"))
                .andDo(print());
        verify(lineService).findAll();
    }

    @DisplayName("지하철 노선 하나 조회")
    @Test
    void showLine() throws Exception {
        // given
        given(lineService.findBy(1L))
                .willReturn(new LineResponse(1L, "2호선", "green"));

        // when
        ResultActions response = mockMvc.perform(get("/lines/1"));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("2호선"))
                .andExpect(jsonPath("$.color").value("green"))
                .andDo(print());
        verify(lineService).findBy(any(Long.class));
    }

    @DisplayName("지하철 노선 정보 업데이트")
    @Test
    void updateLine() throws Exception {
        // given
        String id = "1";
        String content = objectMapper.writeValueAsString(new LineUpdateRequest("2호선", "green"));

        // when
        ResultActions response = mockMvc.perform(put("/lines/" + id)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
        verify(lineService).update(any(Long.class), any(LineUpdateRequest.class));
    }

    @DisplayName("지하철 노선 삭제")
    @Test
    void deleteLine() throws Exception {
        // given
        String id = "1";

        // when
        ResultActions response = mockMvc.perform(delete("/lines/" + id));

        // then
        response.andExpect(status().isNoContent())
                .andDo(print());
        verify(lineService).delete(any(Long.class));
    }
}