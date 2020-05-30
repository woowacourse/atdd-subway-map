package wooteco.subway.admin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.LineResponse;
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
        String lineName = "1호선";
        String preStationName = "잠실역";
        String arrivalStationName = "잠실새내역";
        when(lineService.findByName(lineName)).thenReturn(new Line(lineName, LocalTime.now(), LocalTime.now(), 10, "blue"));
        when(stationService.findByName(preStationName)).thenReturn(new Station(preStationName));
        when(stationService.save(arrivalStationName)).thenReturn(new Station(arrivalStationName));
        doThrow(IllegalStateException.class)
                .when(lineService)
                .addLineStation(eq(null), any());



        LineStationDto lineStationDto = new LineStationDto(lineName, preStationName, arrivalStationName);
        assertThatThrownBy(() -> lineController.registerLineStation(lineStationDto, lineName))
                .isInstanceOf(IllegalStateException.class);

    }
}