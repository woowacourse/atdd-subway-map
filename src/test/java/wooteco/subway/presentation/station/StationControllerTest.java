package wooteco.subway.presentation.station;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wooteco.subway.application.station.StationService;
import wooteco.subway.domain.station.Station;
import wooteco.subway.presentation.station.dto.StationDtoAssembler;
import wooteco.subway.presentation.station.dto.StationRequest;
import wooteco.subway.presentation.station.dto.StationResponse;
import wooteco.util.StationFactory;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {StationController.class})
class StationControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StationDtoAssembler stationDtoAssembler;

    @MockBean
    private StationService stationService;

    @Test
    void createStation_inTrueCase() throws Exception {
        createStation_mockInitialize();

        sendStationRequest_inTrueCase("가역");
    }

    @Test
    void createStation_inFalseCase() throws Exception {
        createStation_mockInitialize();

        sendStationRequest_inFalseCase("");
        sendStationRequest_inFalseCase(null);
    }

    private void createStation_mockInitialize() {
        given(stationService.createStation(any(Station.class)))
                .willReturn(StationFactory.create(1L, "나역"));

        given(stationDtoAssembler.station(any(Station.class)))
                .willReturn(new StationResponse(1L, "나역"));
    }

    private void sendStationRequest_inTrueCase(String stationName) throws Exception {
        mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new StationRequest(stationName)
                )))
                .andExpect(status().isCreated());
    }

    private void sendStationRequest_inFalseCase(String stationName) throws Exception {
        mockMvc.perform(post("/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new StationRequest(stationName)
                )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void showStations() throws Exception {
        showStations_mockInitialize();

        mockMvc.perform(get("/stations")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.ALL))
                .andExpect(jsonPath("$.[0].id", is(1)));
    }

    private void showStations_mockInitialize() {
        given(stationService.findAll()).willReturn(Collections.singletonList(
                StationFactory.create(1L, "가역")
        ));

        given(stationDtoAssembler.station(any(Station.class)))
                .willReturn(new StationResponse(1L, "가역"));
    }

    @Test
    void deleteStation() throws Exception {
        mockMvc.perform(delete("/stations/1"))
                .andExpect(status().isNoContent());
    }

}