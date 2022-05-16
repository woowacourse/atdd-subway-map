package wooteco.subway.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wooteco.subway.Fixtures.BLUE;
import static wooteco.subway.Fixtures.GANGNAM;
import static wooteco.subway.Fixtures.HYEHWA;
import static wooteco.subway.Fixtures.LINE_2;
import static wooteco.subway.Fixtures.LINE_4;
import static wooteco.subway.Fixtures.RED;
import static wooteco.subway.Fixtures.SINSA;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.dto.request.CreateLineRequest;
import wooteco.subway.dto.request.CreateSectionRequest;
import wooteco.subway.dto.request.CreateStationRequest;

@AutoConfigureMockMvc
@SpringBootTest
@Sql("/truncate.sql")
public class LineControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("노선 생성을 요청한다.")
    void create() throws Exception {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final CreateLineRequest request = new CreateLineRequest(LINE_2, RED, upStationId, downStationId, 10);
        final String requestContent = objectMapper.writeValueAsString(request);

        // when
        final ResultActions response = mockMvc.perform(post("/lines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andDo(print());

        // then
        response
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/lines/1"))
                .andExpect(jsonPath("name").value(LINE_2))
                .andExpect(jsonPath("color").value(RED))
                .andExpect(jsonPath("stations[0].id").value(1L))
                .andExpect(jsonPath("stations[0].name").value(HYEHWA))
                .andExpect(jsonPath("stations[1].id").value(2L))
                .andExpect(jsonPath("stations[1].name").value(SINSA));
    }

    @Test
    @DisplayName("노선 목록을 조회한다.")
    void showAll() throws Exception {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        createLine(LINE_2, RED, upStationId, downStationId, 10);
        createLine(LINE_4, BLUE, upStationId, downStationId, 10);

        // when
        final ResultActions response = mockMvc.perform(get("/lines"))
                .andDo(print());

        // then
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(LINE_2))
                .andExpect(jsonPath("$[0].color").value(RED))
                .andExpect(jsonPath("$[0].stations[0].name").value(HYEHWA))
                .andExpect(jsonPath("$[0].stations[1].name").value(SINSA))
                .andExpect(jsonPath("$[1].name").value(LINE_4))
                .andExpect(jsonPath("$[1].color").value(BLUE))
                .andExpect(jsonPath("$[1].stations[0].name").value(HYEHWA))
                .andExpect(jsonPath("$[1].stations[1].name").value(SINSA));
    }

    @Test
    @DisplayName("노선을 조회한다.")
    void show() throws Exception {
        // given
        final Long upStationId = createStation(HYEHWA);
        final Long downStationId = createStation(SINSA);

        final Long lineId = createLine(LINE_2, RED, upStationId, downStationId, 10);

        // when
        final ResultActions response = mockMvc.perform(get("/lines/" + lineId))
                .andDo(print());

        // then
        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(LINE_2))
                .andExpect(jsonPath("color").value(RED))
                .andExpect(jsonPath("stations[0].id").value(upStationId))
                .andExpect(jsonPath("stations[0].name").value(HYEHWA))
                .andExpect(jsonPath("stations[1].id").value(downStationId))
                .andExpect(jsonPath("stations[1].name").value(SINSA));
    }

    @Test
    @DisplayName("없는 노선을 조회하면, 404 상태와 에러 메시지를 응답한다.")
    void showWithNotExistLine() throws Exception {
        // when
        final ResultActions response = mockMvc.perform(get("/lines/" + 10L))
                .andDo(print());

        // then
        response
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("구간을 등록한다.")
    void createSection() throws Exception {
        // given
        final Long stationId1 = createStation(HYEHWA);
        final Long stationId2 = createStation(SINSA);
        final Long stationId3 = createStation(GANGNAM);
        final Long lineId = createLine(LINE_2, RED, stationId1, stationId2, 10);

        final CreateSectionRequest request = new CreateSectionRequest(stationId2, stationId3, 5);
        final String requestContent = objectMapper.writeValueAsString(request);

        // when
        final ResultActions response = mockMvc.perform(post("/lines/" + lineId + "/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andDo(print());

        // then
        response
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("등록하는 구간의 길이가 더 긴 경우, 400 상태와 에러 메시지를 응답한다.")
    void createSectionWithLongDistance() throws Exception {
        // given
        final Long stationId1 = createStation(HYEHWA);
        final Long stationId2 = createStation(SINSA);
        final Long stationId3 = createStation(GANGNAM);
        final Long lineId = createLine(LINE_2, RED, stationId1, stationId3, 10);

        final CreateSectionRequest request = new CreateSectionRequest(stationId1, stationId2, 20);
        final String requestContent = objectMapper.writeValueAsString(request);

        // when
        final ResultActions response = mockMvc.perform(post("/lines/" + lineId + "/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andDo(print());

        // then
        response.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("구간을 삭제한다.")
    void deleteSection() throws Exception {
        // given
        final Long stationId1 = createStation(HYEHWA);
        final Long stationId2 = createStation(SINSA);
        final Long stationId3 = createStation(GANGNAM);
        final Long lineId = createLine(LINE_2, RED, stationId1, stationId2, 10);
        createSection(lineId, stationId2, stationId3, 10);

        // when
        final ResultActions response = mockMvc.perform(delete("/lines/" + lineId + "/sections?stationId=" + stationId1))
                .andDo(print());

        // then
        response
                .andExpect(status().isOk());
    }

    private Long createStation(final String name) throws Exception {
        final CreateStationRequest request = new CreateStationRequest(name);
        final String requestContent = objectMapper.writeValueAsString(request);

        return Long.parseLong(Objects.requireNonNull(mockMvc.perform(post("/stations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestContent))
                        .andReturn()
                        .getResponse()
                        .getHeader("Location"))
                .split("/")[2]);
    }

    private Long createLine(final String name, final String color, final Long upStationId, final Long downStationId,
                            final int distance) throws Exception {
        final CreateLineRequest request = new CreateLineRequest(name, color, upStationId, downStationId, distance);
        final String requestContent = objectMapper.writeValueAsString(request);

        return Long.parseLong(Objects.requireNonNull(mockMvc.perform(post("/lines")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestContent))
                        .andReturn()
                        .getResponse()
                        .getHeader("Location"))
                .split("/")[2]);
    }

    private void createSection(final Long lineId, final Long upStationId, final Long downStationId, final int distance)
            throws Exception {
        final CreateSectionRequest request = new CreateSectionRequest(upStationId, downStationId, distance);
        final String requestContent = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(post("/lines/" + lineId + "/sections")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent));
    }
}
