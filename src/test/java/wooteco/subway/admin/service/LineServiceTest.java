package wooteco.subway.admin.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.jdbc.Sql;

import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.domain.Station;
import wooteco.subway.admin.dto.req.LineStationCreateRequest;
import wooteco.subway.admin.dto.res.LineResponse;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.StationRepository;

@ExtendWith(MockitoExtension.class)
@Sql("/truncate.sql")
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private Line line;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = new Line("2호선", "bg-yellow-400", LocalTime.of(05, 30), LocalTime.of(22, 30), 5);
        lineService = new LineService(lineRepository, stationRepository);

        line.addLineStation(new LineStation(1L, 1L, 10, 10));
        line.addLineStation(new LineStation(2L, 1L, 10, 10));
        line.addLineStation(new LineStation(3L, 2L, 10, 10));
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(4L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsIds().get(0)).isEqualTo(4L);
        assertThat(line.getLineStationsIds().get(1)).isEqualTo(1L);
        assertThat(line.getLineStationsIds().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsIds().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationBetweenTwo() {
        LineStationCreateRequest request = new LineStationCreateRequest(4L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsIds().get(0)).isEqualTo(4L);
        assertThat(line.getLineStationsIds().get(1)).isEqualTo(1L);
        assertThat(line.getLineStationsIds().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsIds().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationAtTheEndOfLine() {
        LineStationCreateRequest request = new LineStationCreateRequest(4L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsIds().get(0)).isEqualTo(4L);
        assertThat(line.getLineStationsIds().get(1)).isEqualTo(1L);
        assertThat(line.getLineStationsIds().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsIds().get(3)).isEqualTo(3L);
    }

    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 1L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsIds().get(0)).isEqualTo(2L);
        assertThat(line.getLineStationsIds().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 2L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsIds().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsIds().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 3L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsIds().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsIds().get(1)).isEqualTo(2L);
    }

    @Test
    void findLineWithStationsById() {
        List<Station> stations = Arrays.asList(new Station("강남역"), new Station("역삼역"),
            new Station("삼성역"));
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        when(stationRepository.findAllByLineId(line.getId())).thenReturn(stations);

        LineResponse lineResponse = lineService.findLineByStationId(line.getId());

        assertThat(lineResponse.getStations()).hasSize(3);
    }
}
