package wooteco.subway.admin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
import wooteco.subway.admin.dto.LineStationCreateRequest;
import wooteco.subway.admin.service.LineService;
import wooteco.subway.admin.service.StationService;

import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LineControllerTest {

    private StationService stationService = mock(StationService.class);
    private LineService lineService = mock(LineService.class);

    private LineController lineController = new LineController(lineService, stationService);

    @Test
    void registerLineStation() {
        Long lineId = 1L;
        Line line = new Line("1호선", LocalTime.now(), LocalTime.now(), 10, "blue");
        LineStationCreateRequest lineStationCreateRequest = new LineStationCreateRequest(null, 1L, 10, 10);
        when(lineService.addLineStation(lineId, lineStationCreateRequest)).thenReturn(line);
        when(stationService.findAllOf(line)).thenReturn(new HashSet<>(Collections.singletonList(new Station("신정역"))));

        ResponseEntity<LineResponse> lineResponse = lineController.registerLineStation(lineId, lineStationCreateRequest);
        assertThat(lineResponse.getStatusCodeValue()).isEqualTo(HttpStatus.CREATED.value());

    }
}