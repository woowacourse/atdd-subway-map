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
import wooteco.subway.service.SectionService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("구간 관련 Controller 테스트")
@WebMvcTest(SectionController.class)
class SectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SectionService sectionService;

    @DisplayName("구간을 생성한다.")
    @Test
    void createSection() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines/{lineId}/sections", 1L)
                .content("{\"upStationId\": 1, \"downStationId\": 2, \"distance\": 10}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @DisplayName("구간 생성 시 상행역 ID 값에 null 을 입력하면 예외가 발생한다.")
    @Test
    void createSectionUpStationIdNull() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines/{lineId}/sections", 1L)
                .content("{\"upStationId\": null, \"downStationId\": 2, \"distance\": 10}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("상행역을 선택해주세요."));
    }

    @DisplayName("구간 생성 시 하행역 ID 값에 null 을 입력하면 예외가 발생한다.")
    @Test
    void createSectionDownStationIdNull() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines/{lineId}/sections", 1L)
                .content("{\"upStationId\": 1, \"downStationId\": null, \"distance\": 10}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("하행역을 선택해주세요."));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    @DisplayName("구간 생성 시 구간 거리에 1 미만의 값을 입력하면 예외가 발생한다.")
    void createSectionDistanceUnder1(int distance) throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .post("/lines/{lineId}/sections", 1L)
                .content("{\"upStationId\": 1, \"downStationId\": 2, \"distance\": " + distance + "}")
                .contentType(MediaType.APPLICATION_JSON);

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("구간 거리는 1 이상이어야 합니다."));
    }

    @DisplayName("구간을 삭제한다.")
    @Test
    void deleteSection() throws Exception {
        // given
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/lines/{lineId}/sections", 1L)
                .param("stationId", String.valueOf(2L));

        // when & then
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }
}
