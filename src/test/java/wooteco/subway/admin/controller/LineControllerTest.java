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
        Long lineId = 1L;

        LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(null, lineId, 10, 10);
        doThrow(IllegalStateException.class)
                .when(lineService)
                .addLineStation(lineId, lineStationCreateRequest);


        assertThatThrownBy(() -> lineController.registerLineStation(lineId, lineStationCreateRequest))
                .isInstanceOf(IllegalStateException.class);

    }
}