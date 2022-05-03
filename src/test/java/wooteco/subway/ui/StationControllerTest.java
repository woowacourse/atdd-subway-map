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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

@WebMvcTest
public class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockedStatic<StationDao> stationDao;

    @BeforeAll
    static void beforeAll() {
        stationDao = mockStatic(StationDao.class);
    }

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() throws Exception {
        // given
        StationRequest test = new StationRequest("test");
        given(StationDao.findByName("test"))
                .willReturn(Optional.empty());
        given(StationDao.save(any(Station.class)))
                .willReturn(new Station(1L, test.getName()));
        // when
        ResultActions perform = mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)));
        // then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("test"))
                .andExpect(header().stringValues("Location", "/stations/1"));
    }

    @DisplayName("지하철 역 생성 시 이름이 중복된다면 에러를 응답한다.")
    @Test
    void createStation_duplication_exception() throws Exception {
        // given
        StationRequest test = new StationRequest("test");
        given(StationDao.findByName("test"))
                .willReturn(Optional.of(new Station(1L, test.getName())));
        // when
        ResultActions perform = mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(test)));
        // then
        perform.andExpect(status().isBadRequest());
    }

    @AfterAll
    static void afterAll() {
        stationDao.close();
    }
}
