package wooteco.subway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wooteco.subway.domain.Line;
import wooteco.subway.domain.Section;
import wooteco.subway.domain.Sections;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.service.LineService;
import wooteco.subway.ui.LineController;

@WebMvcTest(LineController.class)
class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LineService lineService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("노선 생성")
    @Test
    void 노선_생성() throws Exception {
        Section section = new Section(
                new Station(1L, "서울역"),
                new Station(2L, "용산역"), 10);
        Line mockLine = new Line(1L, "1호선", "bg-darkblue-600", new Sections(section));
        given(lineService.save(any(LineRequest.class))).willReturn(mockLine);

        LineRequest lineRequest = new LineRequest("1호선", "bg-darkblue-600", 1L, 2L, 10);

        mockMvc.perform(post("/lines")
                        .content(objectMapper.writeValueAsString(lineRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("name").value("1호선"))
                .andExpect(jsonPath("color").value("bg-darkblue-600"))
                .andExpect(jsonPath("stations[0].name").value("서울역"))
                .andExpect(jsonPath("stations[1].name").value("용산역"))
                .andDo(print());
    }

    @DisplayName("단일 노선 조회")
    @Test
    void 단일_노선_조회() throws Exception {
        Section section1 = new Section(
                new Station(1L, "망원역"),
                new Station(2L, "합정역"), 10);
        Section section2 = new Section(
                new Station(2L, "합정역"),
                new Station(3L, "상수역"), 10);
        Line mockLine = new Line(1L, "6호선", "bg-brown-600",
                new Sections(new LinkedList<>(List.of(section1, section2))));

        given(lineService.findById(1L)).willReturn(mockLine);

        mockMvc.perform(get("/lines/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("6호선"))
                .andExpect(jsonPath("stations[0].id").value("1"))
                .andExpect(jsonPath("stations[0].name").value("망원역"))
                .andExpect(jsonPath("stations[1].id").value("2"))
                .andExpect(jsonPath("stations[1].name").value("합정역"))
                .andExpect(jsonPath("stations[2].id").value("3"))
                .andExpect(jsonPath("stations[2].name").value("상수역"))
                .andDo(print());
    }
}