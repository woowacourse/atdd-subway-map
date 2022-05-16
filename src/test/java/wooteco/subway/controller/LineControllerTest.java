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
import wooteco.subway.service.LineService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("지하철 노선 관련 Controller 테스트")
@WebMvcTest(LineController.class)
class LineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LineService lineService;

    @ParameterizedTest
    @ValueSource(strings = {"null", "\"\"", "\" \""})
    @DisplayName("지하철 노선 생성 시 노선 이름에 null 또는 빈값을 입력하면 예외가 발생한다.")
    void createLineNameBlank(String lineName) throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines")
                .content("{\"name\": " + lineName + ", \"color\": \"bg-red-600\", \"upStationId\": 1, \"downStationId\": 2, \"distance\": 10}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("이름 값을 입력해주세요."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "\"\"", "\" \""})
    @DisplayName("지하철 노선 생성 시 노선 색상에 null 또는 빈값을 입력하면 예외가 발생한다.")
    void createLineColorBlank(String lineColor) throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines")
                .content("{\"name\": \"신분당선\", \"color\": " + lineColor + ", \"upStationId\": 1, \"downStationId\": 2, \"distance\": 10}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("색상 값을 입력해주세요."));
    }

    @DisplayName("지하철 노선 생성 시 상행역 ID 값에 null 을 입력하면 예외가 발생한다.")
    @Test
    void createLineUpStationIdNull() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines")
                .content("{\"name\": \"신분당선\", \"color\": \"bg-red-600\", \"upStationId\": null, \"downStationId\": 2, \"distance\": 10}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상행역을 선택해주세요."));
    }

    @DisplayName("지하철 노선 생성 시 하행역 ID 값에 null 을 입력하면 예외가 발생한다.")
    @Test
    void createLineDownStationIdNull() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines")
                .content("{\"name\": \"신분당선\", \"color\": \"bg-red-600\", \"upStationId\": 1, \"downStationId\": null, \"distance\": 10}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("하행역을 선택해주세요."));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    @DisplayName("지하철 노선 생성 시 구간 거리에 1 미만의 값을 입력하면 예외가 발생한다.")
    void createLineDistanceUnder1(int distance) throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines")
                .content("{\"name\": \"신분당선\", \"color\": \"bg-red-600\", \"upStationId\": 1, \"downStationId\": 2, \"distance\": " + distance + "}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("구간 거리는 1 이상이어야 합니다."));
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .get("/lines");

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void showLine() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .get("/lines/{id}", 1L);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .put("/lines/{id}", 1L)
                .content("{\"name\": \"신분당선\", \"color\": \"bg-red-600\"}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/lines/{id}", 1L);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
    }
}
