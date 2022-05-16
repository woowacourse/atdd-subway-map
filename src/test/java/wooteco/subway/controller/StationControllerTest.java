package wooteco.subway.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wooteco.subway.service.StationService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("지하철역 관련 Controller 테스트")
@WebMvcTest(StationController.class)
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationService stationService;

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/stations")
                .content("{\"name\": \"강남역\"}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("강남역"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "\"\"", "\" \""})
    @DisplayName("지하철역 생성 시 지하철역 이름에 null 또는 빈값을 입력하면 예외가 발생한다.")
    void createStationBlank(String stationName) throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/stations")
                .content("{\"name\": " + stationName + "}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이름 값을 입력해주세요."));
    }

    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void showStations() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .get("/stations");

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @DisplayName("지하철역을 삭제한다.")
    @Test
    void deleteStation() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/stations/{id}", 1L);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
