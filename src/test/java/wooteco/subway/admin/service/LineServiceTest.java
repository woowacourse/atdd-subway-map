package wooteco.subway.admin.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import wooteco.subway.admin.domain.Line;
import wooteco.subway.admin.domain.LineStation;
import wooteco.subway.admin.repository.LineRepository;
import wooteco.subway.admin.repository.LineStationRepository;
import wooteco.subway.admin.repository.StationRepository;

import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private LineStationRepository lineStationRepository;

    private Line line;
    private LineService lineService;

    @BeforeEach
    void setUp() {
        line = new Line(1L, "2호선", LocalTime.of(05, 30), LocalTime.of(22, 30), 5, "bg-gray-300");
        lineService = new LineService(lineRepository, lineStationRepository, stationRepository);

        line.addLineStation(new LineStation(null, 1L, 10, 10));
        line.addLineStation(new LineStation(1L, 2L, 10, 10));
        line.addLineStation(new LineStation(2L, 3L, 10, 10));
    }

    @Test
    void addLineStationAtTheFirstOfLine() {
        LineStation request = new LineStation(0L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(4L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationBetweenTwo() {
        LineStation request = new LineStation(1L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(4L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(3L);
    }

    @Test
    void addLineStationAtTheEndOfLine() {
        LineStation request = new LineStation(3L, 4L, 10, 10);

        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.addLineStation(line.getId(), request);

        assertThat(line.getStations()).hasSize(4);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(2)).isEqualTo(3L);
        assertThat(line.getLineStationsId().get(3)).isEqualTo(4L);
    }

    @Test
    void removeLineStationAtTheFirstOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 1L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(2L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationBetweenTwo() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 2L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(3L);
    }

    @Test
    void removeLineStationAtTheEndOfLine() {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        lineService.removeLineStation(line.getId(), 3L);

        assertThat(line.getStations()).hasSize(2);
        assertThat(line.getLineStationsId().get(0)).isEqualTo(1L);
        assertThat(line.getLineStationsId().get(1)).isEqualTo(2L);
    }

    @Test
    void updateLineStation(){
        line.updatePreStationWhenAdd(new LineStation(null,5L,10,10));
    }

}
