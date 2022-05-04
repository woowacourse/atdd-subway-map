package wooteco.subway.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
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
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@WebMvcTest(LineController.class)
public class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LineDao lineDao;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() throws Exception {
        // given
        LineRequest test = new LineRequest("test", "GREEN");
        given(lineDao.findByName("test"))
                .willReturn(Optional.empty());
        given(lineDao.save(any(Line.class)))
                .willReturn(new Line(1L, test.getName(), test.getColor()));
        // when
        ResultActions perform = mockMvc.perform(post("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)));
        // then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("test"))
                .andExpect(jsonPath("color").value("GREEN"))
                .andExpect(header().stringValues("Location", "/lines/1"));
    }

    @DisplayName("지하철 노선 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void createLine_duplication_exception() throws Exception {
        // given
        LineRequest test = new LineRequest("test", "GREEN");
        given(lineDao.findByName("test"))
                .willReturn(Optional.of(new Line(1L, test.getName(), test.getColor())));
        // when
        ResultActions perform = mockMvc.perform(post("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)));
        // then
        perform.andExpect(status().isBadRequest());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() throws Exception {
        // given
        given(lineDao.findAll())
                .willReturn(List.of(new Line(1L, "test1", "GREEN"), new Line(2L, "test2", "YELLOW")));
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
        given(lineDao.findById(1L))
                .willReturn(Optional.of(new Line(1L, "test1", "GREEN")));
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
        given(lineDao.findById(1L))
                .willReturn(Optional.empty());
        // when
        ResultActions perform = mockMvc.perform(get("/lines/1"));
        // then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("해당 ID의 지하철 노선이 존재하지 않습니다."));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() throws Exception {
        // given
        given(lineDao.findById(1L))
                .willReturn(Optional.of(new Line(1L, "test", "BLACK")));
        // when
        ResultActions perform = mockMvc.perform(delete("/lines/1"));
        // then
        perform.andExpect(status().isNoContent());
    }

    @DisplayName("삭제 요청 시 ID에 해당하는 지하철 노선이 없다면 에러를 응답한다.")
    @Test
    void deleteLine_noExistLine_exception() throws Exception {
        // given
        given(lineDao.findById(1L))
                .willReturn(Optional.empty());
        // when
        ResultActions perform = mockMvc.perform(delete("/lines/1"));
        // then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").value("해당 ID의 지하철 노선이 존재하지 않습니다."));
    }

    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
                .willReturn(Optional.of(new Line(1L, "11호선", "GRAY")));
        given(lineDao.findByName("9호선")).willReturn(Optional.empty());
        // when
        ResultActions perform = mockMvc.perform(put("/lines/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));
        // then
        perform.andExpect(status().isOk());
    }

    @DisplayName("존재하지 않는 ID의 노선을 수정한다.")
    @Test
    void updateLine_noExistLine_Exception() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
                .willReturn(Optional.empty());
        // when
        ResultActions perform = mockMvc.perform(put("/lines/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));
        // then
        perform.andExpect(status().isBadRequest());
    }

    @DisplayName("중복된 이름으로 노선을 수정한다.")
    @Test
    void updateLine_duplicateName_Exception() throws Exception {
        // given
        LineRequest updateRequest = new LineRequest("9호선", "GREEN");
        given(lineDao.findById(1L))
                .willReturn(Optional.of(new Line(1L, "11호선", "GRAY")));
        given(lineDao.findByName("9호선"))
                .willReturn(Optional.of(new Line(2L, "9호선", "BLUE")));
        // when
        ResultActions perform = mockMvc.perform(put("/lines/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)));
        // then
        perform.andExpect(status().isBadRequest());
    }
}
