package wooteco.subway.ui;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.dao.LineDao;
import wooteco.subway.domain.Line;
import wooteco.subway.dto.LineRequest;

@WebMvcTest
public class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockedStatic<LineDao> lineDao;

    @BeforeAll
    static void beforeAll() {
        lineDao = mockStatic(LineDao.class);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() throws Exception {
        // given
        LineRequest test = new LineRequest("test", "GREEN");
        given(LineDao.findByName("test"))
                .willReturn(Optional.empty());
        given(LineDao.save(any(Line.class)))
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
        given(LineDao.findByName("test"))
                .willReturn(Optional.of(new Line(1L, test.getName(), test.getColor())));
        // when
        ResultActions perform = mockMvc.perform(post("/lines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)));
        // then
        perform.andExpect(status().isBadRequest());
    }
}
