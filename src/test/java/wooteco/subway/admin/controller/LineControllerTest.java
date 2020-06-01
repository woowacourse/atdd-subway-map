package wooteco.subway.admin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.dto.LineStationDto;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class LineControllerTest {

    private StationService stationService = mock(StationService.class);
    private LineService lineService = mock(LineService.class);

    private LineController lineController = new LineController(lineService, stationService);

    @Test
    void registerLineStation() {
        Line line = new Line("1호선", LocalTime.now(), LocalTime.now(), 10, "blue");
        LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(line.getName(), null, "preStationName", 1L, "stationName", 10, 10);

        when(lineService.findByName(lineStationCreateRequest.getLineName())).thenReturn(line);
        when(stationService.findByName(lineStationCreateRequest.getPreStationName())).thenReturn(new Station(lineStationCreateRequest.getPreStationName()));
        when(stationService.findOrRegister(lineStationCreateRequest.getStationName())).thenReturn(new Station(lineStationCreateRequest.getStationName()));
        doThrow(IllegalStateException.class)
                .when(lineService)
                .addLineStation(eq(line.getId()), any());


        assertThatThrownBy(() -> lineController.registerLineStation(lineStationCreateRequest))
                .isInstanceOf(IllegalStateException.class);

    }
}