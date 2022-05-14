package wooteco.subway.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StationDao stationDao;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("역 생성")
    @Test
    void 역_생성() throws Exception {
        Station station = new Station("선릉역");

        StationRequest request = new StationRequest(station.getName());

        mockMvc.perform(post("/stations")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("name").value("선릉역"))
                .andDo(print());
    }

    @DisplayName("이미 존재하는 이름의 역 생성 시 400처리")
    @Test
    void 존재하는_역_생성_400예외() throws Exception {
        Station station = new Station("선릉역");
        stationDao.save(station);

        StationRequest request = new StationRequest("선릉역");

        mockMvc.perform(post("/stations")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("존재하는 모든 역 조회")
    @Test
    void 모든_역_조회() throws Exception {
        stationDao.save(new Station("선릉역"));
        stationDao.save(new Station("역삼역"));
        stationDao.save(new Station("강남역"));
        stationDao.save(new Station("잠실역"));

        mockMvc.perform(get("/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("선릉역"))
                .andExpect(jsonPath("$[1].name").value("역삼역"))
                .andExpect(jsonPath("$[2].name").value("강남역"))
                .andExpect(jsonPath("$[3].name").value("잠실역"))
                .andDo(print());
    }

    @DisplayName("단일 역 삭제")
    @Test
    void 단일_역_삭제() throws Exception {
        Station station = stationDao.save(new Station("선릉역"));

        mockMvc.perform(delete("/stations/" + station.getId()))
                .andExpect(status().isNoContent());

        assertThatThrownBy(() -> stationDao.findById(station.getId()))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }
}
