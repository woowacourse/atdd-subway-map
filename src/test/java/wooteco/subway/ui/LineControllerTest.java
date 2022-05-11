package wooteco.subway.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.dto.LineRequest;
import wooteco.subway.dto.LineResponse;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.service.LineService;

@WebMvcTest(LineController.class)
public class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LineService lineService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() throws Exception {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN");
        LineResponse lineResponse = new LineResponse(1L, "test", "GREEN");
        given(lineService.createLine(any(LineRequest.class)))
            .willReturn(lineResponse);
        // when
        ResultActions perform = mockMvc.perform(post("/lines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(lineRequest)));
        // then
        perform.andExpect(status().isCreated())
            .andExpect(jsonPath("id").value(1))
            .andExpect(jsonPath("name").value("test"))
            .andExpect(jsonPath("color").value("GREEN"))
            .andExpect(header().stringValues("Location", "/lines/1"));
    }

    @DisplayName("지하철 노선 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void createLine_duplicate_name_exception() throws Exception {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN");
        given(lineService.createLine(any()))
            .willThrow(new IllegalArgumentException("중복되는 이름의 지하철 노선이 존재합니다."));
        // when
        ResultActions perform = mockMvc.perform(post("/lines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(lineRequest)));
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("message").value("중복되는 이름의 지하철 노선이 존재합니다."));
    }

    @DisplayName("지하철 노선 생성 시 색깔이 중복된다면 에러를 응답한다.")
    @Test
    void createLine_duplicate_color_exception() throws Exception {
        // given
        LineRequest lineRequest = new LineRequest("test", "GREEN");
        given(lineService.createLine(any()))
            .willThrow(new IllegalArgumentException("중복되는 색깔의 지하철 노선이 존재합니다."));
        // when
        ResultActions perform = mockMvc.perform(post("/lines")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(lineRequest)));
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("message").value("중복되는 색깔의 지하철 노선이 존재합니다."));
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() throws Exception {
        // given
        given(lineService.showLines())
            .willReturn(List.of(new LineResponse(1L, "test1", "GREEN"), new LineResponse(2L, "test2", "YELLOW")));
        // when
        ResultActions perform = mockMvc.perform(get("/lines"));
        // then
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("test1"))
            .andExpect(jsonPath("$[0].color").value("GREEN"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("test2"))
            .andExpect(jsonPath("$[1].color").value("YELLOW"));
    }

    @DisplayName("id를 이용해 지하철 노선을 조회한다.")
    @Test
    void getLine() throws Exception {
        // given
        given(lineService.showLine(1L))
            .willReturn(new LineResponse(1L, "test1", "GREEN"));
        // when
        ResultActions perform = mockMvc.perform(get("/lines/1"));
        // then
        perform.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("id").value(1))
            .andExpect(jsonPath("name").value("test1"))
            .andExpect(jsonPath("color").value("GREEN"));
    }

    @DisplayName("존재하지 않는 id를 이용해 지하철 노선을 조회할 경우 에러가 발생한다.")
    @Test
    void getLine_noExistLine_exception() throws Exception {
        // given
        given(lineService.showLine(1L))
            .willThrow(new IllegalArgumentException("해당하는 ID의 지하철 노선이 존재하지 않습니다."));
        // when
        ResultActions perform = mockMvc.perform(get("/lines/1"));
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("message").value("해당하는 ID의 지하철 노선이 존재하지 않습니다."));
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        doNothing()
            .when(lineService)
            .updateLine(anyLong(), any(LineRequest.class));
        // when
        ResultActions perform = mockMvc.perform(put("/lines/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)));
        // then
        perform.andExpect(status().isOk());
    }

    @DisplayName("존재하지 않는 ID의 노선을 수정한다면 예외가 발생한다.")
    @Test
    void updateLine_noExistLine_Exception() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        doThrow(new IllegalArgumentException("해당하는 ID의 지하철 노선이 존재하지 않습니다."))
            .when(lineService)
            .updateLine(anyLong(), any(LineRequest.class));
        // when
        ResultActions perform = mockMvc.perform(put("/lines/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)));
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("message").value("해당하는 ID의 지하철 노선이 존재하지 않습니다."));
    }

    @DisplayName("중복된 이름으로 노선을 수정한다.")
    @Test
    void updateLine_duplicate_name_exception() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        doThrow(new IllegalArgumentException("중복되는 이름의 지하철 노선이 존재합니다."))
            .when(lineService)
            .updateLine(anyLong(), any(LineRequest.class));
        // when
        ResultActions perform = mockMvc.perform(put("/lines/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)));
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("message").value("중복되는 이름의 지하철 노선이 존재합니다."));
    }

    @DisplayName("중복된 색깔로 노선을 수정한다.")
    @Test
    void updateLine_duplicate_color_exception() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        doThrow(new IllegalArgumentException("중복되는 색깔의 지하철 노선이 존재합니다."))
            .when(lineService)
            .updateLine(any(), any());
        // when
        ResultActions perform = mockMvc.perform(put("/lines/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)));
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("message").value("중복되는 색깔의 지하철 노선이 존재합니다."));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() throws Exception {
        // given
        doNothing()
            .when(lineService)
            .deleteLine(anyLong());
        // when
        ResultActions perform = mockMvc.perform(delete("/lines/1"));
        // then
        perform.andExpect(status().isNoContent());
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철 노선이 없다면 에러를 응답한다.")
    @Test
    void deleteLine_noExistLine_exception() throws Exception {
        // given
        doThrow(new IllegalArgumentException("해당하는 ID의 지하철 노선이 존재하지 않습니다."))
            .when(lineService)
            .deleteLine(anyLong());
        // when
        ResultActions perform = mockMvc.perform(delete("/lines/1"));
        // then
        perform.andExpect(status().isBadRequest())
            .andExpect(jsonPath("message").value("해당하는 ID의 지하철 노선이 존재하지 않습니다."));
    }

    @DisplayName("노선 구간에 새로운 구간을 등록한다.")
    @Test
    void addSection() throws Exception {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        doNothing()
            .when(lineService)
            .addSection(anyLong(), any(SectionRequest.class));
        // when
        ResultActions perform = mockMvc.perform(post("/lines/1/sections")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sectionRequest)));
        // then
        perform.andExpect(status().isOk());
    }

    @DisplayName("노선 구간 목록 중 지하철역 ID를 통해 삭제한다.")
    @Test
    void deleteSection() throws Exception {
        // given
        SectionRequest sectionRequest = new SectionRequest(1L, 2L, 10);
        doNothing()
            .when(lineService)
            .addSection(anyLong(), any(SectionRequest.class));
        // when
        ResultActions perform = mockMvc.perform(delete("/lines/1/sections?stationId=2")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(sectionRequest)));
        // then
        perform.andExpect(status().isOk());
    }
}
