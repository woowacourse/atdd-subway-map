package wooteco.subway.line.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wooteco.subway.domain.Station;
import wooteco.subway.station.StationDao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LineApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StationDao stationDao;


    @Test
    @DisplayName("노선 생성 - 성공")
    void createLine_success() throws Exception {
        Long upStationId = stationDao.save(Station.from("잠실역")).getId();
        Long downStationId = stationDao.save(Station.from("잠실새내역")).getId();

        final LineRequest lineRequest =
            new LineRequest("2호선", "bg-green-600", upStationId, downStationId, 10);

        mockMvc.perform(post("/lines")
            .content(objectMapper.writeValueAsString(lineRequest))
            .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("id").exists())
            .andExpect(jsonPath("name").value("2호선"))
            .andExpect(jsonPath("color").value("bg-green-600"))
            .andExpect(jsonPath("stations").isArray())
            .andExpect(jsonPath("stations[0].name").value("잠실역"))
            .andExpect(jsonPath("stations[1].name").value("잠실새내역"));
    }

    @Test
    @DisplayName("노선 생성 - 실패(노선 중복 이름)")
    void createLine_duplicatedName() throws Exception {

    }

    @Test
    @DisplayName("노선 생성 - 실패(노선 중복 컬러)")
    void createLine_duplicatedColor() throws Exception {

    }

    @Test
    @DisplayName("노선 생성 - 실패(request 필수값 누락)")
    void createLine_notSatisfiedRequest() throws Exception {

    }

    @Test
    @DisplayName("노선 생성 - 실패(등록되지 않는 역을 노선 종점역에 등록할 때)")
    void createLine_notExistStation() throws Exception {

    }

    @Test
    @DisplayName("노선 생성 - 실패(상행선과 하행선 역이 같을 경우)")
    void createLine_sameStations() throws Exception {

    }
}
