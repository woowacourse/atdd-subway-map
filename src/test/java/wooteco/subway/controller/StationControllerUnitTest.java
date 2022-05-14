package wooteco.subway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.service.StationService;
import wooteco.subway.ui.StationController;

@WebMvcTest(StationController.class)
class StationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("역 생성")
    @Test
    void 역_생성() throws Exception {
        Station mock = new Station(1L, "선릉역");
        given(stationService.save(any(Station.class))).willReturn(mock);

        StationRequest request = new StationRequest("선릉역");

        mockMvc.perform(post("/stations")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("id").value(1))
                .andExpect(jsonPath("name").value("선릉역"))
                .andDo(print());
    }

    @DisplayName("이미 존재하는 역 생성 시 400처리")
    @Test
    void 존재하는_역_생성_400예외() throws Exception {
        given(stationService.save(any(Station.class))).willThrow(DuplicateKeyException.class);

        StationRequest request = new StationRequest("선릉역");

        mockMvc.perform(post("/stations")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @DisplayName("모든 역 조회")
    @Test
    void 모든_역_조회() throws Exception {
        List<Station> mock = List.of(
                new Station(1L, "선릉역"),
                new Station(2L, "역삼역"),
                new Station(3L, "강남역"));
        given(stationService.findAll()).willReturn(mock);

        mockMvc.perform(get("/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("선릉역"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("역삼역"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("강남역"))
                .andDo(print());

    }

    @DisplayName("단일 역 삭제")
    @Test
    void 단일_역_삭제() throws Exception {
        mockMvc.perform(delete("/stations/1"))
                .andExpect(status().isNoContent());
    }
}