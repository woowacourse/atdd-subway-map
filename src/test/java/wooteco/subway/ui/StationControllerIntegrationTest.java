package wooteco.subway.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wooteco.subway.Fixtures.HYEHWA;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import wooteco.subway.dto.request.CreateStationRequest;

@AutoConfigureMockMvc
@SpringBootTest
@Sql("/truncate.sql")
public class StationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("역 생성을 요청한다.")
    void create() throws Exception {
        // given
        final CreateStationRequest request = new CreateStationRequest(HYEHWA);
        final String requestContent = objectMapper.writeValueAsString(request);

        // when
        final ResultActions response = mockMvc.perform(post("/stations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                .andDo(print());

        // then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("name").value(HYEHWA));
    }

    @Test
    @DisplayName("역 목록을 조회한다.")
    void showAll() throws Exception {
        // given
        createStation(HYEHWA);

        // when
        final ResultActions response = mockMvc.perform(get("/stations"))
                .andDo(print());

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(HYEHWA));
    }

    @Test
    @DisplayName("역을 삭제한다.")
    void deleteStation() throws Exception {
        // given
        final Long stationId = createStation(HYEHWA);

        // when
        final ResultActions response = mockMvc.perform(delete("/stations/" + stationId))
                .andDo(print());

        // then
        response.andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("없는 역을 삭제할 경우, 404 상태와 에러 메시지를 응답한다.")
    void deleteWithNotExistStation() throws Exception {
        // when
        final ResultActions response = mockMvc.perform(delete("/stations/" + 10L))
                .andDo(print());

        // then
        response.andExpect(status().isNotFound());
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
}
